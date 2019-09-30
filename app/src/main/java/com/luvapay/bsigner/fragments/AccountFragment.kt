package com.luvapay.bsigner.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseFragment
import com.luvapay.bsigner.entities.StellarAccount
import com.luvapay.bsigner.items.AccountItem
import com.luvapay.bsigner.unSubscribe
import com.luvapay.bsigner.utils.selectionListener
import com.luvapay.bsigner.viewmodel.HomeViewModel
import com.mikepenz.fastadapter.ISelectionListener
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.query
import io.objectbox.reactive.DataSubscription
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountFragment : BaseFragment() {

    //val vm: HomeViewModel by sharedViewModel()

    internal val accountAdapter by lazy { FastItemAdapter<AccountItem>() }
    private lateinit var accountSub: DataSubscription

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

        accountAdapter.getSelectExtension().apply {
            isSelectable = arguments?.getBoolean(SELECTABLE) ?: false
            multiSelect = arguments?.getBoolean(MULTI_SELECTABLE) ?: false
            selectOnLongClick = false
            selectWithItemUpdate = true

            selectionListener = selectionListener { item, selected ->
                accountListener?.onSelectAccount(mutableListOf(item.account))
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //Listener
        if (context is AccountListener) accountListener = context
        //Subscription
        accountSub = AppBox.accountBox.query {}.subscribe().on(AndroidScheduler.mainThread()).observer { accounts ->
            //Coroutine
            viewLifecycleOwner.lifecycleScope.launch {
                val accountItems = withContext(Dispatchers.Default) {
                    return@withContext accounts.map { AccountItem(it) }
                }
                accountAdapter.set(accountItems)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        accountListener = null
        accountSub.unSubscribe()
    }

    interface AccountListener {
        fun onSelectAccount(accounts: MutableList<StellarAccount>)
    }

    companion object {
        fun init(selectable: Boolean = false, multiSelect: Boolean = false): AccountFragment {
            val args = Bundle().apply {
                putBoolean(SELECTABLE, selectable)
                putBoolean(MULTI_SELECTABLE, multiSelect)
            }
            return AccountFragment().apply { arguments = args }
        }

        private const val SELECTABLE =  "SELECTABLE"
        private const val MULTI_SELECTABLE = "MULTI_SELECTABLE"
    }

}