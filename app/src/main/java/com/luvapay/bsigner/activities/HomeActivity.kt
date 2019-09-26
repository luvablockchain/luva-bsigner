package com.luvapay.bsigner.activities

import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.items.AccountItem
import com.luvapay.bsigner.unSubscribe
import com.luvapay.bsigner.viewmodel.HomeViewModel
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.query
import io.objectbox.reactive.DataSubscription
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.activity_home.activityHome_accountList as accountList

class HomeActivity : BaseActivity() {

    private val vm: HomeViewModel by viewModel()

    private val accountAdapter by lazy { FastItemAdapter<AccountItem>() }
    private var accountSub: DataSubscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        accountList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(this@HomeActivity, RecyclerView.VERTICAL, false)
            adapter = accountAdapter.apply {

            }
        }

        accountSub = AppBox.accountBox.query {}.subscribe().on(AndroidScheduler.mainThread()).observer { accounts ->
            accountAdapter.set(accounts.map { AccountItem(it) })
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        accountSub?.unSubscribe()
    }

}
