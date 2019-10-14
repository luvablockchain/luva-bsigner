package com.luvapay.bsigner.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.account.AccountDetailActivity
import com.luvapay.bsigner.activities.account.BackupWarningActivity
import com.luvapay.bsigner.activities.account.RecoverAccountActivity
import com.luvapay.bsigner.entities.Ed25519Signer
import com.luvapay.bsigner.items.AccountItem
import com.luvapay.bsigner.unSubscribe
import com.luvapay.bsigner.utils.getColorCompat
import com.luvapay.bsigner.utils.gone
import com.luvapay.bsigner.utils.visible
import com.luvapay.bsigner.viewmodel.HomeViewModel
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.query
import io.objectbox.reactive.DataSubscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlinx.android.synthetic.main.fragment_home_account.view.fragmentHomeAccount_accountList as accountList
import kotlinx.android.synthetic.main.fragment_home_account.view.fragmentHomeAccount_modifyBtn as modifyBtn
import kotlinx.android.synthetic.main.fragment_home_account.view.fragmentHomeAccount_createBtn as createAccountBtn
import kotlinx.android.synthetic.main.fragment_home_account.view.fragmentHomeAccount_recoverBtn as recoverAccountBtn
import kotlinx.android.synthetic.main.fragment_home_account.view.fragmentHomeAccount_menuContainer as menuContainer

class HomeFragment : Fragment() {

    private val vm: HomeViewModel by sharedViewModel()

    private val accountAdapter by lazy { FastItemAdapter<AccountItem>() }
    private lateinit var accountSub: DataSubscription

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
        = inflater.inflate(R.layout.fragment_home_account, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.accountList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            //Adapter
            adapter = accountAdapter.apply {
                onClickListener = { _, _, item, _ ->
                    context?.startActivity<AccountDetailActivity>(Ed25519Signer.OBJ_ID to item.account.objId)
                    true
                }
            }
        }

        view.modifyBtn.setOnClickListener {
            vm.canModify.value = !(vm.canModify.value ?: true)
        }

        view.createAccountBtn.setOnClickListener {
            context?.startActivity<BackupWarningActivity>()
        }

        view.recoverAccountBtn.setOnClickListener {
            context?.startActivity<RecoverAccountActivity>()
        }

        vm.canModify.observe(this, Observer { canModify ->
            //Modify Button
            view.modifyBtn.apply {
                setBackgroundColor(
                    context.getColorCompat(if (canModify) R.color.colorPrimary else R.color.colorAccent)
                )
                icon = context.getDrawable(
                    if (canModify) R.drawable.ic_check else R.drawable.ic_edit
                )
            }
            //Account menu
            view.menuContainer.apply {
                if (canModify) visible() else gone()
            }
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //Subscription
        accountSub = AppBox.ed25519SignerBox.query {}.subscribe().on(AndroidScheduler.mainThread()).observer { accounts ->
            //Coroutine
            lifecycleScope.launch {
                val accountItems = withContext(Dispatchers.Default) {
                    return@withContext accounts.map { AccountItem(it).apply { canModify = vm.canModify.value ?: false } }
                }
                accountAdapter.set(accountItems)
            }
        }

        vm.canModify.observe(this, Observer { canModify ->
            accountAdapter.adapterItems.forEach { it.canModify = canModify }
            accountAdapter.notifyDataSetChanged()
        })
    }

    override fun onDetach() {
        super.onDetach()
        accountSub.unSubscribe()
    }

}
