package com.luvapay.bsigner.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.fragments.HomeFragment
import com.luvapay.bsigner.fragments.SettingsFragment
import com.luvapay.bsigner.fragments.TransactionFragment
import com.luvapay.bsigner.viewmodel.HomeViewModel
import com.orhanobut.logger.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

        supportFragmentManager.commit {
            add(R.id.activityMain_fragmentContainer, homeFrag)
            hide(homeFrag)
            add(R.id.activityMain_fragmentContainer, transactionFrag)
            hide(transactionFrag)
            add(R.id.activityMain_fragmentContainer, settingsFrag)
            hide(settingsFrag)
        }
    Logger.d(getString(R.string.settings))
        bottomNav.apply {
            //Listener
            setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        supportFragmentManager.commit {
                            hide(transactionFrag)
                            hide(settingsFrag)
                            show(homeFrag)
                        }
                    }
                    R.id.nav_transactions -> {
                        supportFragmentManager.commit {
                            hide(homeFrag)
                            hide(settingsFrag)
                            show(transactionFrag)
                        }
                    }
                    R.id.nav_settings -> {
                        supportFragmentManager.commit {
                            hide(transactionFrag)
                            hide(homeFrag)
                            show(settingsFrag)
                        }
                    }
                    else -> return@setOnNavigationItemSelectedListener false
                }
                true
            }
            //Default item
            selectedItemId = R.id.nav_home
        }

        lifecycleScope.launch {
            delay(1000)
            //recreate()
        }
    }

}