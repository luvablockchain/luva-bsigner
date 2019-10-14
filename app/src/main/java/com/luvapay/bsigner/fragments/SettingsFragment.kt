package com.luvapay.bsigner.fragments

import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseFragment
import com.luvapay.bsigner.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SettingsFragment : BaseFragment() {

    override val layout: Int = R.layout.fragment_home_settings
    private val vm: HomeViewModel by sharedViewModel()


}
