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
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.query
import io.objectbox.reactive.DataSubscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.android.synthetic.main.fragment_account.view.fragmentAccount_accountList as accountList

class AccountFragment : BaseFragment() {

    //val vm: HomeViewModel by sharedViewModel()

    private val accountAdapter by lazy { FastItemAdapter<AccountItem>() }
    private lateinit var accountSub: DataSubscription

    private var accountListener: AccountListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
        = inflater.inflate(R.layout.fragment_account, container, false).apply {

        accountList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = accountAdapter
        }

        accountAdapter.getSelectExtension().apply {
            isSelectable = arguments?.getBoolean(SELECTABLE) ?: false
            multiSelect = arguments?.getBoolean(MULTI_SELECT) ?: false
            selectOnLongClick = false
            selectWithItemUpdate = true

            selectionListener = selectionListener { item, _ ->
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

    fun getSelectedAccount() = accountAdapter.getSelectExtension().selectedItems.map { it.account.publicKey }.toList()

    interface AccountListener {
        fun onSelectAccount(accounts: MutableList<StellarAccount>)
    }

    companion object {
        fun init(selectable: Boolean = false, multiSelect: Boolean = false): AccountFragment {
            val args = Bundle().apply {
                putBoolean(SELECTABLE, selectable)
                putBoolean(MULTI_SELECT, multiSelect)
            }
            return AccountFragment().apply { arguments = args }
        }

        private const val SELECTABLE =  "SELECTABLE"
        internal const val MULTI_SELECT = "MULTI_SELECT"
    }

}