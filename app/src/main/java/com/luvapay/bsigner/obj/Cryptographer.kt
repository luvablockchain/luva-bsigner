package com.luvapay.bsigner.obj

import android.security.keystore.KeyGenParameterSpec
import android.util.Base64
import com.luvapay.bsigner.utils.Prefs
import java.security.KeyStore
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object Cryptographer {

    /*private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    internal val aesCipher: Cipher = Cipher.getInstance(AES_CIPHER)

    fun init() {
        (Prefs contains DEFAULT_IV_PREF).otherwise {
            val randomIv = ByteArray(16)
            SecureRandom().nextBytes(randomIv)
            Prefs.commit {
                putString(DEFAULT_IV_PREF, randomIv.encodeBase64())
            }
        }
        (Prefs contains DEFAULT_SALT_PREF).otherwise {
            val salt = ByteArray(32)
            SecureRandom().nextBytes(salt)
            Prefs.commit {
                putString(DEFAULT_SALT_PREF, salt.encodeBase64())
            }
        }

        initSymmetricKey()
    }

    private fun initSymmetricKey() {

        if (keyStore.containsAlias(DEFAULT_SYMMETRIC_ALIAS)) return

        val startTime = Calendar.getInstance().time
        val endTime = Calendar.getInstance().apply { add(Calendar.YEAR, 24) }.time
        val keySize = 256

        val keyGenParamSpec = KeyGenParameterSpec.Builder(
            DEFAULT_SYMMETRIC_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).run {
            setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            setKeySize(keySize)
            setRandomizedEncryptionRequired(false)
            setKeyValidityStart(startTime)
            setKeyValidityEnd(endTime)
            setCertificateNotBefore(startTime)
            setCertificateNotAfter(endTime)
            setCertificateSerialNumber(BigInteger(256, Random()))
            setDigests(
                KeyProperties.DIGEST_SHA256,
                KeyProperties.DIGEST_SHA512
            )
            build()
        }
        KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        ).run {
            init(keyGenParamSpec)
            generateKey()
        }
    }

    fun getKey(): SecretKey {
        if (keyStore.containsAlias(DEFAULT_SYMMETRIC_ALIAS)) {
            return keyStore.getKey(DEFAULT_SYMMETRIC_ALIAS, null) as SecretKey
        } else {
            throw Exception("$DEFAULT_SYMMETRIC_ALIAS key doesn't exist")
        }
    }

    fun encrypt(textToEncrypt: String): String {
        return getKey().encrypt(textToEncrypt.toByteArray()).encodeBase64()
    }

    fun decrypt(encryptedText: String): String {
        return String(getKey().decrypt(encryptedText.decodeBase64()))
    }*/
}

/*
fun SecretKey.encrypt(bytes: ByteArray): ByteArray {
    val iv = Prefs.getString(DEFAULT_IV_PREF).decodeBase64()
    //Encrypt Byte Array
    Cryptographer.aesCipher.init(Cipher.ENCRYPT_MODE, this@encrypt, IvParameterSpec(iv))
    return Cryptographer.aesCipher.doFinal(bytes)
}

fun SecretKey.decrypt(encryptedBytes: ByteArray): ByteArray {
    val iv = Prefs.getString(DEFAULT_IV_PREF).decodeBase64()
    //Decrypt Byte Array
    Cryptographer.aesCipher.init(Cipher.DECRYPT_MODE, this@decrypt, IvParameterSpec(iv))
    return Cryptographer.aesCipher.doFinal(encryptedBytes)
}

fun ByteArray.encodeBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)
fun String.decodeBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP)

fun initCryptographer() { Cryptographer.init() }

internal const val DEFAULT_SYMMETRIC_ALIAS = "DefaultSymmetric"

internal const val AES_CIPHER = "AES/CBC/PKCS7Padding"

private const val DEFAULT_IV_PREF = "default_iv_pref"
const val DEFAULT_SALT_PREF = "default_salt_pref"
private const val CRYPTOGRAPHER_PREFS = "cryptographer_prefs"

internal const val ANDROID_KEYSTORE = "AndroidKeyStore"
internal const val KEY_AES_ALGORITHM = "AES"*/
