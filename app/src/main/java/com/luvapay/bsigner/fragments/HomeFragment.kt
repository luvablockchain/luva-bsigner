package com.luvapay.bsigner.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.account.AccountDetailActivity
import com.luvapay.bsigner.entities.StellarAccount
import com.luvapay.bsigner.items.AccountItem
import com.luvapay.bsigner.unSubscribe
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.query
import io.objectbox.reactive.DataSubscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.startActivity
import kotlinx.android.synthetic.main.fragment_home.view.fragmentHome_accountList as accountList

class HomeFragment : Fragment() {

    private val accountAdapter by lazy { FastItemAdapter<AccountItem>() }
    private lateinit var accountSub: DataSubscription

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
        = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.accountList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = accountAdapter
        }

        accountAdapter.onClickListener = { _, _, item, _ ->
            context?.startActivity<AccountDetailActivity>(StellarAccount.OBJ_ID to item.account.objId)
            true
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //Subscription
        accountSub = AppBox.accountBox.query {}.subscribe().on(AndroidScheduler.mainThread()).observer { accounts ->
            //Coroutine
            lifecycleScope.launch {
                val accountItems = withContext(Dispatchers.Default) {
                    return@withContext accounts.map { AccountItem(it) }
                }
                accountAdapter.set(accountItems)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        accountSub.unSubscribe()
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

}
