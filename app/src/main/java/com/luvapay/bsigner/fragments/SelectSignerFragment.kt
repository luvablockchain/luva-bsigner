package com.luvapay.bsigner.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseFragment
import com.luvapay.bsigner.entities.Ed25519Signer
import com.luvapay.bsigner.items.SignerSelectItem
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
import kotlinx.android.synthetic.main.fragment_signer_select.view.fragmentSignerSelect_signerList as signerList

class SelectSignerFragment : BaseFragment() {

    override val layout: Int = R.layout.fragment_signer_select

    private val signerAdapter by lazy { FastItemAdapter<SignerSelectItem>() }
    private lateinit var accountSub: DataSubscription

    private var signerSelectListener: SignerSelectListener? = null
    private var signerClickListener: SignerClickListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.signerList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = signerAdapter
        }

        if (arguments?.getBoolean(SELECTABLE) == true) {
            signerAdapter.getSelectExtension().apply {
                isSelectable = arguments?.getBoolean(SELECTABLE) ?: false
                multiSelect = arguments?.getBoolean(MULTI_SELECT) ?: false
                selectOnLongClick = false
                selectWithItemUpdate = true

                selectionListener = selectionListener { item, _ ->
                    signerSelectListener?.onSignerSelected(mutableListOf(item.account))
                }
            }
        } else {
            signerAdapter.onClickListener = { _, _, item, _ ->
                signerClickListener?.onSignerClicked(item.account)
                true
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //Listeners
        when (context) {
            is SignerSelectListener -> signerSelectListener = context
            is SignerClickListener-> signerClickListener = context
        }
        //Subscription
        accountSub = AppBox.ed25519SignerBox.query {}.subscribe().on(AndroidScheduler.mainThread()).observer { accounts ->
            //Coroutine
            lifecycleScope.launch {
                val accountItems = withContext(Dispatchers.Default) {
                    return@withContext accounts.map { SignerSelectItem(it) }
                }
                signerAdapter.set(accountItems)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        signerSelectListener = null
        signerClickListener = null
        accountSub.unSubscribe()
    }

    fun getSelectedSigner() = signerAdapter.getSelectExtension().selectedItems.map { it.account }.toList()

    interface SignerSelectListener {
        fun onSignerSelected(accounts: MutableList<Ed25519Signer>)
    }

    interface SignerClickListener {
        fun onSignerClicked(account: Ed25519Signer)
    }

    companion object {
        @JvmStatic
        fun init(selectable: Boolean = false, multiSelect: Boolean = false): SelectSignerFragment {
            val args = Bundle().apply {
                putBoolean(SELECTABLE, selectable)
                putBoolean(MULTI_SELECT, multiSelect)
            }
            return SelectSignerFragment().apply { arguments = args }
        }

        private const val SELECTABLE =  "SELECTABLE"
        internal const val MULTI_SELECT = "MULTI_SELECT"
    }

}