package com.luvapay.bsigner.activities.transaction

import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.entities.TransactionInfo
import com.luvapay.bsigner.entities.TransactionInfo_
import com.luvapay.bsigner.items.SignatureItem
import com.luvapay.bsigner.utils.prefetchText
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.orhanobut.logger.Logger
import io.objectbox.kotlin.query
import org.stellar.sdk.Network
import org.stellar.sdk.PaymentOperation
import org.stellar.sdk.Transaction
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_toolbar as toolbar
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_amountTv as amountTv
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_fromTv as fromTv
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_memoTv as memoTv
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_signBtn as signBtn
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_signatureList as signatureList
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_toTv as toTv

class TransactionDetailActivity : BaseActivity() {

    private lateinit var signatureAdapter: FastItemAdapter<SignatureItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_detail)

        toolbar.init()

        val transactionXdr = intent.getStringExtra(TransactionInfo.XDR) ?: ""
        val transactionName = intent.getStringExtra(TransactionInfo.NAME) ?: ""

        val transactionObjId = intent.getLongExtra("objId", -2)

        /*if (transactionXdr.isBlank()) {
            Logger.d("transactionXdr is blank")
            finish()
            return
        }*/

        val cachedTransaction = AppBox.transactionInfoBox[transactionObjId]
        val transaction = Transaction.fromEnvelopeXdr(cachedTransaction.envelopXdrBase64, Network.TESTNET)

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

        signatureAdapter = FastItemAdapter()
        signatureList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(this@TransactionDetailActivity, RecyclerView.HORIZONTAL, false)
        }
    }

}
