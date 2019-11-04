package com.luvapay.bsigner

import android.app.Application
import com.luvapay.bsigner.entities.*
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.objectbox.reactive.DataSubscription
import org.stellar.sdk.KeyPair

object AppBox {

    lateinit var ed25519SignerBox: Box<Ed25519Signer>
    lateinit var transactionInfoBox: Box<TransactionInfo>
    lateinit var transactionSignerBox: Box<TransactionSigner>

    fun init(boxStore: BoxStore) {
        boxStore.run {
            ed25519SignerBox = boxFor()
            transactionInfoBox = boxFor()
            transactionSignerBox = boxFor()
        }
    }

    fun addAccount(mnemonics: String, accountNumber: Int = 0, accountAdded: () -> Unit, accountExists: () -> Unit, error: (throwable: Throwable) -> Unit) {
        runCatching {
            val keyPair = KeyPair.fromBip39Seed(createBip39Seed(mnemonics.toCharArray()), accountNumber)
            val addedAccount = ed25519SignerBox.query {
                equal(
                    Ed25519Signer_.publicKey, keyPair.accountId
                )
            }.findFirst()
            return@runCatching if (addedAccount == null) {
                val account = Ed25519Signer(
                    publicKey = keyPair.accountId,
                    privateKey = String(keyPair.secretSeed),
                    mnemonic = mnemonics
                )
                ed25519SignerBox.put(
                    account
                )
                Result.success(true)
            } else {
                Result.success(false)
            }
        }.onSuccess {
            when (it.getOrThrow()) {
                true -> accountAdded()
                false -> accountExists()
            }
        }.onFailure {
            it.printStackTrace()
            error(it)
        }
    }
}

fun Application.initAppBox() {
    AppBox.init(MyObjectBox.builder().androidContext(this@initAppBox).build())
}

fun DataSubscription.unSubscribe() { this.takeUnless { it.isCanceled }?.run { cancel() } }