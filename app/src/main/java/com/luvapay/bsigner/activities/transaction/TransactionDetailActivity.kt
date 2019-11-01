package com.luvapay.bsigner.activities.transaction

import android.os.Bundle
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.entities.TransactionInfo
import com.luvapay.bsigner.utils.prefetchText
import com.orhanobut.logger.Logger
import org.stellar.sdk.Network
import org.stellar.sdk.PaymentOperation
import org.stellar.sdk.Transaction
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_amountTv as amountTv
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_cancelBtn as cancelBtn
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_fromTv as fromTv
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_memoTv as memoTv
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_signBtn as signBtn
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_signerList as signerList
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_toTv as toTv

class TransactionDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_detail)


        val transactionXdr = intent.getStringExtra(TransactionInfo.XDR) ?: ""
        val transactionName = intent.getStringExtra(TransactionInfo.NAME) ?: ""

        if (transactionXdr.isBlank()) {
            Logger.d("transactionXdr is blank")
            finish()
            return
        }

        val transaction = Transaction.fromEnvelopeXdr(transactionXdr, Network.TESTNET)

        fromTv prefetchText transaction.sourceAccount
        kotlin.runCatching {
            transaction.operations.firstOrNull()?.let { operation ->
                val transactionOperation = operation as PaymentOperation
                fromTv prefetchText transaction.sourceAccount
                toTv prefetchText transactionOperation.destination
                amountTv prefetchText transactionOperation.amount
            }
            memoTv prefetchText (transaction.memo.toString())
        }

    }

}
