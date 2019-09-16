package com.luvapay.multisignature

import org.stellar.sdk.*

object Horizon {

    val server = Server("https://horizon-testnet.stellar.org")

    fun init() {

    }

}

fun testnetTransaction(sourceAccount: TransactionBuilderAccount, block: Transaction.Builder.() -> Unit = {}): Transaction
        = Transaction.Builder(sourceAccount, Network.TESTNET).setOperationFee(100).apply(block).build()

fun nativePaymentOperation(destination: String, amount: String, block: PaymentOperation.Builder.() -> Unit = {}): PaymentOperation
        = PaymentOperation.Builder(
                destination,
                AssetTypeNative(),
                amount
            ).apply(block).build()