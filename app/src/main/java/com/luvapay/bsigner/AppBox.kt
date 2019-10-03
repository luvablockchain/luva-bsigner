package com.luvapay.bsigner

import android.app.Application
import android.os.Build
import com.luvapay.bsigner.entities.MyObjectBox
import com.luvapay.bsigner.entities.StellarAccount
import com.luvapay.bsigner.entities.StellarAccount_
import com.luvapay.bsigner.utils.encrypt
import com.luvapay.bsigner.utils.getCryptKey
import com.orhanobut.logger.Logger
import com.sonhvp.kryptographer.Kryptographer
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.objectbox.reactive.DataSubscription
import org.stellar.sdk.KeyPair

object AppBox {

    lateinit var accountBox: Box<StellarAccount>

    fun init(boxStore: BoxStore) {
        boxStore.run {
            accountBox = boxFor()
        }
    }

    fun addAccount(mnemonics: String, accountNumber: Int = 0, accountAdded: () -> Unit, accountExists: () -> Unit, error: (throwable: Throwable) -> Unit) {
        runCatching {
            val keyPair = KeyPair.fromBip39Seed(createBip39Seed(mnemonics.toCharArray()), accountNumber)
            val addedAccount = accountBox.query {
                equal(
                    StellarAccount_.publicKey, keyPair.accountId
                )
            }.findFirst()
            return@runCatching if (addedAccount == null) {
                val account = StellarAccount(
                    publicKey = keyPair.accountId,
                    privateKey = keyPair.secretSeed.toString(),
                    mnemonic = mnemonics
                )
                Logger.d("${account.publicKey}")
                accountBox.put(
                    account
                ).also {
                    Logger.d("${accountBox[it].publicKey}")
                }
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