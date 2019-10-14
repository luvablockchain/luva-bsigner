package com.luvapay.bsigner.activities.multisign

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.luvapay.bsigner.*
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_multisign_sign_transaction.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
import java.nio.CharBuffer
import java.security.MessageDigest
import java.util.*

class SignTransactionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multisign_sign_transaction)

        if (intent == null) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        val signerObjIds = intent.getLongArrayExtra(PickSignerActivity.EXTRA_SIGNER_OBJ_IDS)?.toMutableList() ?: mutableListOf()
        val transactionXdr = intent.getStringExtra(PickSignerActivity.EXTRA_TRANSACTION_XDR) ?: "change later"

        if (signerObjIds.isEmpty() || transactionXdr == null) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        val ed25519Signers = AppBox.ed25519SignerBox.get(signerObjIds).toMutableList()


        Logger.d(ed25519Signers.map { it.privateKey })

        test.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply { putExtra("signature", "testSignature") })
            finish()
        }

        lifecycleScope.launch {
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

                //Receive xdr convert to Transaction Object and hash
                val transactionTxHash = transaction.hash()

                val signatures = arrayListOf<String>()
                val signatureHints = arrayListOf<String>()

                ed25519Signers.forEach { ed25519Signer ->
                    val keyPair = KeyPair.fromSecretSeed(ed25519Signer.privateKey)

                    val signatureDecorated = keyPair.signDecorated(transactionTxHash)
                    signatures.add(String(signatureDecorated.signature.signature))
                    signatureHints.add(String(signatureDecorated.hint.signatureHint))
                }

                Logger.d(signatures)
                Logger.d(signatureHints)


                try {
                    //Logger.d(Horizon.server.submitTransaction(transaction).isSuccess)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}
