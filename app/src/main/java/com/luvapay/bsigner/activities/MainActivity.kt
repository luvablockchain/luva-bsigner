package com.luvapay.bsigner.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.fragments.HomeFragment
import com.luvapay.bsigner.fragments.SettingsFragment
import com.luvapay.bsigner.fragments.TransactionFragment
import com.luvapay.bsigner.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.activity_home_main.activityMain_nav as bottomNav

class MainActivity : BaseActivity() {

    private val vm: HomeViewModel by viewModel()

    private val homeFrag: HomeFragment by lazy { HomeFragment() }
    private val transactionFrag: TransactionFragment by lazy { TransactionFragment() }
    private val settingsFrag: SettingsFragment by lazy { SettingsFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_main)

        bottomNav.apply {
            //Listener
            setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        homeFrag.replace()
                    }
                    R.id.nav_transactions -> {
                        transactionFrag.replace()
                    }
                    R.id.nav_settings -> {
                        settingsFrag.replace()
                    }
                    else -> return@setOnNavigationItemSelectedListener false
                }
                true
            }
            //Default item
            selectedItemId = R.id.nav_home
        }
    }

    private fun Fragment.replace() {
        supportFragmentManager.commit {
            replace(R.id.activityMain_fragmentContainer, this@replace)
        }
    }

}