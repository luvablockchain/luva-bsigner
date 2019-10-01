package com.luvapay.bsigner

import android.app.Application
import com.luvapay.bsigner.entities.MyObjectBox
import com.luvapay.bsigner.entities.StellarAccount
import com.luvapay.bsigner.entities.StellarAccount_
import com.luvapay.bsigner.utils.ifNull
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

    fun addAccount(mnemonics: String, accountNumber: Int = 0): Boolean {
        val keyPair = KeyPair.fromBip39Seed(createBip39Seed(mnemonics.toCharArray()), accountNumber)
        val addedAccount = accountBox.query {
            equal(
                StellarAccount_.publicKey, keyPair.accountId
            )
        }.findFirst()
        return if (addedAccount != null) {
            accountBox.put(StellarAccount(keyPair.accountId, keyPair.secretSeed.toString(), mnemonics))
            true
        } else {
            false
        }
    }
}

fun Application.initAppBox() {
    AppBox.init(MyObjectBox.builder().androidContext(this@initAppBox).build())
}

fun DataSubscription.unSubscribe() { this.takeUnless { it.isCanceled }?.run { cancel() } }