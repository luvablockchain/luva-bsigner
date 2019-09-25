package com.luvapay.bsigner

import android.app.Application
import com.luvapay.bsigner.entities.MyObjectBox
import com.luvapay.bsigner.entities.StellarAccount
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.reactive.DataSubscription

object AppBox {

    lateinit var accountBox: Box<StellarAccount>

    fun init(boxStore: BoxStore) {
        boxStore.run {
            accountBox = boxFor()
        }
    }
}

fun Application.initAppBox() {
    AppBox.init(MyObjectBox.builder().androidContext(this@initAppBox).build())
}

fun DataSubscription.unSubscribe() { this.takeUnless { it.isCanceled }?.run { cancel() } }