package com.luvapay.bsigner.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.transaction.TransactionDetailActivity
import com.luvapay.bsigner.base.BaseFragment
import com.luvapay.bsigner.items.TransactionItem
import com.luvapay.bsigner.unSubscribe
import com.luvapay.bsigner.viewmodel.HomeViewModel
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.orhanobut.logger.Logger
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.query
import io.objectbox.reactive.DataSubscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.stellar.sdk.Network
import org.stellar.sdk.Transaction
import kotlinx.android.synthetic.main.fragment_home_transaction.view.fragmentHomeTransaction_transactionList as transactionList

class TransactionFragment : BaseFragment() {

    override val layout: Int = R.layout.fragment_home_transaction
    private val vm: HomeViewModel by sharedViewModel()

    private lateinit var transactionAdapter: FastItemAdapter<TransactionItem>
    private lateinit var transactionSub: DataSubscription

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionAdapter = FastItemAdapter()
        transactionAdapter.onClickListener = { _, _, item, _ ->
            context?.startActivity<TransactionDetailActivity>("transactionXdr" to item.transaction.toEnvelopeXdrBase64())
            true
        }

        view.transactionList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = transactionAdapter
        }

    }

    override fun onResume() {
        super.onResume()
        transactionSub = AppBox.transactionInfoBox.query {  }.subscribe().on(AndroidScheduler.mainThread()).onError {

        }.observer { transactionInfoList ->
            lifecycleScope.launch {
                val items = withContext(Dispatchers.Default) {
                    return@withContext transactionInfoList.map { TransactionItem(it.objId, Transaction.fromEnvelopeXdr(it.envelopXdrBase64, Network.TESTNET)) }
                }
                transactionAdapter.set(items)
                //Logger.d(transactionInfoList)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        transactionSub.unSubscribe()
    }

    companion object {
        const val TAG = "transactionFragment"
    }

}