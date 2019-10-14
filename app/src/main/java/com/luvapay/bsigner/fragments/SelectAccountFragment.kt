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
import com.luvapay.bsigner.entities.Ed25519Signer
import com.luvapay.bsigner.items.AccountSelectItem
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
import kotlinx.android.synthetic.main.fragment_external_account_select.view.fragmentAccount_accountList as accountList

class SelectAccountFragment : BaseFragment() {

    private val accountAdapter by lazy { FastItemAdapter<AccountSelectItem>() }
    private lateinit var accountSub: DataSubscription

    private var accountSelectListener: AccountSelectListener? = null
    private var accountClickListener: AccountClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
        = inflater.inflate(R.layout.fragment_external_account_select, container, false).apply {

        accountList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = accountAdapter
        }

        if (arguments?.getBoolean(SELECTABLE) == true) {
            accountAdapter.getSelectExtension().apply {
                isSelectable = arguments?.getBoolean(SELECTABLE) ?: false
                multiSelect = arguments?.getBoolean(MULTI_SELECT) ?: false
                selectOnLongClick = false
                selectWithItemUpdate = true

                selectionListener = selectionListener { item, _ ->
                    accountSelectListener?.onAccountSelected(mutableListOf(item.account))
                }
            }
        } else {
            accountAdapter.onClickListener = { _, _, item, _ ->
                accountClickListener?.onAccountClicked(item.account)
                true
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //Listeners
        when (context) {
            is AccountSelectListener -> accountSelectListener = context
            is AccountClickListener-> accountClickListener = context
        }
        //Subscription
        accountSub = AppBox.ed25519SignerBox.query {}.subscribe().on(AndroidScheduler.mainThread()).observer { accounts ->
            //Coroutine
            lifecycleScope.launch {
                val accountItems = withContext(Dispatchers.Default) {
                    return@withContext accounts.map { AccountSelectItem(it) }
                }
                accountAdapter.set(accountItems)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        accountSelectListener = null
        accountClickListener = null
        accountSub.unSubscribe()
    }

    fun getSelectedAccount() = accountAdapter.getSelectExtension().selectedItems.map { it.account }.toList()
    fun getAccounts() = accountAdapter.adapterItems

    interface AccountSelectListener {
        fun onAccountSelected(accounts: MutableList<Ed25519Signer>)
    }

    interface AccountClickListener {
        fun onAccountClicked(account: Ed25519Signer)
    }

    companion object {
        @JvmStatic
        fun init(selectable: Boolean = false, multiSelect: Boolean = false): SelectAccountFragment {
            val args = Bundle().apply {
                putBoolean(SELECTABLE, selectable)
                putBoolean(MULTI_SELECT, multiSelect)
            }
            return SelectAccountFragment().apply { arguments = args }
        }

        private const val SELECTABLE =  "SELECTABLE"
        internal const val MULTI_SELECT = "MULTI_SELECT"
    }

}