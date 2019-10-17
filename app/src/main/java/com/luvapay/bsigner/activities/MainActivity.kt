package com.luvapay.bsigner.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_main)

        supportFragmentManager.commitNow(allowStateLoss = false) {
            supportFragmentManager.findFragmentByTag(HomeFragment.TAG)?.let { remove(it) }
            supportFragmentManager.findFragmentByTag(TransactionFragment.TAG)?.let { remove(it) }
            supportFragmentManager.findFragmentByTag(SettingsFragment.TAG)?.let { remove(it) }
        }

        val homeFrag = HomeFragment()
        val transactionFrag = TransactionFragment()
        val settingsFrag = SettingsFragment()

        supportFragmentManager.commit {
            add(R.id.activityMain_fragmentContainer, homeFrag, HomeFragment.TAG)
            hide(homeFrag)
            add(R.id.activityMain_fragmentContainer, transactionFrag, TransactionFragment.TAG)
            hide(transactionFrag)
            add(R.id.activityMain_fragmentContainer, settingsFrag, SettingsFragment.TAG)
            hide(settingsFrag)
        }

        bottomNav.apply {
            //Listener
            setOnNavigationItemSelectedListener { item ->
                vm.navSelectedItemId = item.itemId
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
            selectedItemId = vm.navSelectedItemId
        }

    }

}