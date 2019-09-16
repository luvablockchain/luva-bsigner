package com.luvapay.multisignature.activities

import android.os.Bundle
import com.luvapay.multisignature.R
import com.luvapay.multisignature.base.BaseActivity
import com.orhanobut.logger.Logger
import com.soneso.stellarmnemonics.Wallet
import com.soneso.stellarmnemonics.derivation.Ed25519Derivation
import kotlinx.android.synthetic.main.activity_backup.*
import org.jetbrains.anko.startActivity
import org.stellar.sdk.KeyPair
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class BackupActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)

        btnNext.setOnClickListener {
            startActivity<CreateMnemonicsActivity>()
            finish()
        }

        val testMnemonics = Wallet.generate24WordMnemonic()
        val mnemonics = "bench hurt jump file august wise shallow faculty impulse spring exact slush thunder author capable act festival slice deposit sauce coconut afford frown better".toCharArray()
        val words = String(mnemonics).split(" ")

        val keyPair0 = Wallet.createKeyPair(mnemonics, "ad".toCharArray(), 0)
        val keyPair1 = Wallet.createKeyPair(mnemonics, null, 1)

        /*Logger.d(
            "\n${keyPair0.accountId}\n${String(keyPair0.secretSeed)}"
        )
        Logger.d(
            "\n${keyPair1.accountId}\n${String(keyPair1.secretSeed)}"
        )

        "abc1kad".toCharArray().toBytes()*/

        val bip39Seed = createBip39Seed(mnemonics)
        Logger.d(
            "${createKeyPair(bip39Seed, 0).string()}"
        )
        Logger.d(
            "${createKeyPair(bip39Seed, 1).string()}"
        )

        Logger.d("${KeyPair.fromBip39Seed(bip39Seed, 0).string()}")
        Logger.d("${KeyPair.fromBip39Seed(bip39Seed, 1).string()}")
    }

    fun createKeyPair(bip39Seed: ByteArray, index: Int, passphrase: CharArray = "".toCharArray()): KeyPair {
        /*val account = Ed25519Derivation.fromSecretSeed(bip39Seed).run {
            derived(44)
            derived(148)
            derived(index)
        }*/
        val masterPrivateKey = Ed25519Derivation.fromSecretSeed(bip39Seed)
        val purpose = masterPrivateKey.derived(44)
        val coinType = purpose.derived(148)
        val account = coinType.derived(index)
        return KeyPair.fromSecretSeed(account.privateKey)
    }

    fun createBip39Seed(mnemonic: CharArray, passphrase: ByteArray = "".toByteArray()): ByteArray {
        val keySpec = PBEKeySpec(mnemonic, "abc".toByteArray(), 2048, 512)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        return secretKeyFactory.generateSecret(keySpec).encoded
    }

    fun KeyPair.string(): String = "\n$accountId\n${String(secretSeed)}"
}
