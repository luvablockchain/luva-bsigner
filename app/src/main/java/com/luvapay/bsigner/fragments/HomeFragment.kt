package com.luvapay.bsigner.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.signer.SignerDetailActivity
import com.luvapay.bsigner.activities.signer.BackupWarningActivity
import com.luvapay.bsigner.activities.signer.RecoverSignerActivity
import com.luvapay.bsigner.base.BaseFragment
import com.luvapay.bsigner.entities.Ed25519Signer
import com.luvapay.bsigner.items.SignerItem
import com.luvapay.bsigner.unSubscribe
import com.luvapay.bsigner.utils.getColorCompat
import com.luvapay.bsigner.utils.gone
import com.luvapay.bsigner.utils.visible
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
import kotlinx.android.synthetic.main.fragment_home_signer.view.fragmentHomeAccount_accountList as accountList
import kotlinx.android.synthetic.main.fragment_home_signer.view.fragmentHomeAccount_modifyBtn as modifyBtn
import kotlinx.android.synthetic.main.fragment_home_signer.view.fragmentHomeAccount_createBtn as createAccountBtn
import kotlinx.android.synthetic.main.fragment_home_signer.view.fragmentHomeAccount_recoverBtn as recoverAccountBtn
import kotlinx.android.synthetic.main.fragment_home_signer.view.fragmentHomeAccount_menuContainer as menuContainer

class HomeFragment : BaseFragment() {

    override val layout: Int = R.layout.fragment_home_signer

    private val vm: HomeViewModel by sharedViewModel()

    private val signerAdapter by lazy { FastItemAdapter<SignerItem>() }
    private lateinit var accountSub: DataSubscription

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.accountList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            //Adapter
            adapter = signerAdapter.apply {
                onClickListener = { _, _, item, _ ->
                    context?.startActivity<SignerDetailActivity>(Ed25519Signer.OBJ_ID to item.account.objId)
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
            context?.startActivity<RecoverSignerActivity>()
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
                    return@withContext accounts.map { SignerItem(it).apply { canModify = vm.canModify.value ?: false } }
                }
                signerAdapter.set(accountItems)
                Logger.d("${accounts.map { it.privateKey }}")
            }
        }

        vm.canModify.observe(this, Observer { canModify ->
            signerAdapter.adapterItems.forEach { it.canModify = canModify }
            signerAdapter.notifyDataSetChanged()
        })
    }

    override fun onDetach() {
        super.onDetach()
        accountSub.unSubscribe()
    }

    companion object {
        const val TAG = "homeFragment"
    }

}
