package com.luvapay.bsigner.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseFragment
import com.luvapay.bsigner.entities.StellarAccount
import com.luvapay.bsigner.items.AccountItem
import com.luvapay.bsigner.unSubscribe
import com.luvapay.bsigner.viewmodel.HomeViewModel
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.query
import io.objectbox.reactive.DataSubscription
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountFragment : BaseFragment() {

    val vm: HomeViewModel by sharedViewModel()

    private val accountAdapter by lazy { FastItemAdapter<AccountItem>() }
    private var accountSub: DataSubscription? = null

    private var accountListener: AccountListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
        = inflater.inflate(R.layout.fragment_account, container, false).apply {
        fragmentAccount_accountList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = accountAdapter
        }

        accountAdapter.apply {
            onClickListener = { v, adapter, item, position ->
                accountListener?.onSelectAccount(item.account)
                true
            }
        }

        accountSub = AppBox.accountBox.query {}.subscribe().on(AndroidScheduler.mainThread()).observer { accounts ->
            accountAdapter.set(accounts.map { AccountItem(it) })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AccountListener) accountListener = context
    }

    override fun onDetach() {
        super.onDetach()
        accountListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        accountSub?.unSubscribe()
    }

    interface AccountListener {
        fun onSelectAccount(account: StellarAccount)
    }

}

