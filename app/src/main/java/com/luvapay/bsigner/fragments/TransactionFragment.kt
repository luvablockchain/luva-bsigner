package com.luvapay.bsigner.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseFragment
import com.luvapay.bsigner.unSubscribe
import com.luvapay.bsigner.viewmodel.HomeViewModel
import com.orhanobut.logger.Logger
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.query
import io.objectbox.reactive.DataSubscription
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlinx.android.synthetic.main.fragment_home_transaction.view.fragmentHomeTransaction_transactionList as transactionList

class TransactionFragment : BaseFragment() {

    override val layout: Int = R.layout.fragment_home_transaction
    private val vm: HomeViewModel by sharedViewModel()

    private lateinit var transactionSub: DataSubscription

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.transactionList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

    }

    override fun onResume() {
        super.onResume()
        transactionSub = AppBox.transactionInfoBox.query {  }.subscribe().on(AndroidScheduler.mainThread()).onError {

        }.observer { transactionInfoList ->
            Logger.d(transactionInfoList)
        }
    }

    override fun onPause() {
        super.onPause()
        transactionSub.unSubscribe()
    }

}