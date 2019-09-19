package com.luvapay.bsigner

import android.os.Bundle
import com.luvapay.bsigner.activities.BackupActivity
import com.luvapay.bsigner.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.startActivity
import org.stellar.sdk.FormatException
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Transaction
import org.stellar.sdk.xdr.XdrDataOutputStream
import shadow.com.google.common.io.BaseEncoding
import shadow.net.i2p.crypto.eddsa.EdDSAEngine
import shadow.net.i2p.crypto.eddsa.EdDSAPrivateKey
import shadow.net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable
import shadow.net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.nio.CharBuffer
import java.security.MessageDigest
import java.util.*

class MainActivity : BaseActivity() {

    private val key1 = KeyPair.fromSecretSeed("SA44JVCA3B5HCWPDBJO5AZ7VYYOKIFCD77EAB7G5JYIIQ7HZTQAUONZS")
    private val key2 = KeyPair.fromSecretSeed("SAL4AAOMCRYQLXDZQXNYVNHJNCFKBHSF4V7GAIUBW2HK5GTKMDC2V4VK")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        launch {
            withContext(Dispatchers.IO) {
                val sourceAccount = Horizon.server.accounts().account("GDICGWXEFFJJKGBOH7LL45PPPA6ZFVHEG3PCXP4BUAJHAA6FIFIVG4LJ")

                val transaction = testnetTransaction(sourceAccount) {
                    addOperation(
                        nativePaymentOperation(
                            "GC53FZQZFQ6J5ZABVQDZKEQWU42P3SK4Y7RNOKZ3JKVCCNAKMSE6S2CW",
                            "10"
                        )
                    )
                    .setTimeout(300)
                }

                //Logger.d(transaction.toEnvelopeXdrBase64())

                //Receive xdr convert to Transaction Object and hash
                val transactionTxHash = transaction.hash()

                val sign1 = key1.signDecorated(transactionTxHash)
                val sign2 = key2.signDecorated(transactionTxHash)


                transaction.signatures.add(sign1)
                transaction.signatures.add(sign2)

                //Logger.d(transaction.toEnvelopeXdrBase64())

                transaction.toBase64XDR()

                try {
                    //Logger.d(Horizon.server.submitTransaction(transaction).isSuccess)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        home_create.setOnClickListener {
            startActivity<BackupActivity>()
        }

    }

    private fun KeyPair.signature(txHash: ByteArray): ByteArray {
        val keySpec = EdDSAPrivateKeySpec(secretSeed.getDataBytes(), EdDSANamedCurveTable.ED_25519_CURVE_SPEC)
        val key = EdDSAPrivateKey(keySpec)

        val sgr = EdDSAEngine(MessageDigest.getInstance("SHA-512"))
        sgr.initSign(key)
        sgr.update(txHash)
        return sgr.sign()
    }

    private fun KeyPair.signatureHint(): ByteArray = Arrays.copyOfRange(publicKey, publicKey.size - 4, publicKey.size)

    fun Transaction.toBase64XDR() {

        val outputStream = ByteArrayOutputStream()
        val xdrOutputStream = XdrDataOutputStream(outputStream)

        //Write transaction bytes to stream first
        org.stellar.sdk.xdr.Transaction.encode(xdrOutputStream, this.toXdr())

        //Write number of keys to stream
        xdrOutputStream.writeInt(2)

        //Sign all other keys

        //Transaction txHash
        val txHash = this.hash()

        //First key
        val firstKeySignature = key1.signature(txHash)
        val firstKeySignatureHint = key1.signatureHint()
        xdrOutputStream.write(firstKeySignatureHint)
        xdrOutputStream.writeInt(firstKeySignature.size)
        xdrOutputStream.write(firstKeySignature)

        //Second Key
        val secondKeySignature = key2.signature(txHash)
        val secondKeySignatureHint = key2.signatureHint()
        xdrOutputStream.write(secondKeySignatureHint)
        xdrOutputStream.writeInt(secondKeySignature.size)
        xdrOutputStream.write(secondKeySignature)

        val base64Encoding = BaseEncoding.base64()
        val base64Xdr = base64Encoding.encode(outputStream.toByteArray())
        //Logger.d("$base64Xdr")
    }


    fun CharArray.getDataBytes(): ByteArray {

        //Convert char array to byte array and check
        val bytes = map { it.toByte() }.toTypedArray()
        bytes.forEach { require(it <= 127) { "Illegal characters in encoded char array." } }

        //Decode string private key
        val base32Encoding = BaseEncoding.base32().upperCase().omitPadding()
        val decoded = base32Encoding.decode(CharBuffer.wrap(this@getDataBytes))

        //Payload is bytes[0] -> bytes[53]
        val payload = Arrays.copyOfRange(decoded, 0, decoded.size - 2)

        //Data is bytes[1] -> bytes[53]
        val data = Arrays.copyOfRange(payload, 1, payload.size)

        //Checksum is bytes[54] and bytes[55]
        val checksum = Arrays.copyOfRange(decoded, decoded.size - 2, decoded.size)

        //Check checksum
        val expectedChecksum = calculateChecksum(payload)
        if (!Arrays.equals(expectedChecksum, checksum)) throw FormatException("Checksum invalid")

        //Clear other bytes
        Arrays.fill(bytes, 0.toByte())
        Arrays.fill(decoded, 0.toByte())
        Arrays.fill(payload, 0.toByte())

        return data
    }

    fun calculateChecksum(bytes: ByteArray): ByteArray {
        var crc = 0
        var count: Int = bytes.size
        var i = 0
        var code: Int

        while (count > 0) {
            code = crc.ushr(8) and 0xFF
            code = code xor (bytes[i++].toInt() and 0xFF)
            code = code xor code.ushr(4)
            crc = crc shl 8 and 0xFFFF
            crc = crc xor code
            code = code shl 5 and 0xFFFF
            crc = crc xor code
            code = code shl 7 and 0xFFFF
            crc = crc xor code
            count--
        }
        return byteArrayOf(crc.toByte(), crc.ushr(8).toByte())
    }

}