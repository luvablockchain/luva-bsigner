package com.luvapay.bsigner.activities

import android.os.Bundle
import androidx.fragment.app.commit
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.entities.StellarAccount
import com.luvapay.bsigner.fragments.AccountFragment
import com.luvapay.bsigner.items.AccountItem
import com.luvapay.bsigner.unSubscribe
import com.luvapay.bsigner.viewmodel.HomeViewModel
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import io.objectbox.reactive.DataSubscription
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : BaseActivity(), AccountFragment.AccountListener {

    private val vm: HomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportFragmentManager.commit {
            replace(R.id.activityHome_fragmentContainer, AccountFragment())
        }

    }

    override fun onSelectAccount(accounts: MutableList<StellarAccount>) {

    }

}
