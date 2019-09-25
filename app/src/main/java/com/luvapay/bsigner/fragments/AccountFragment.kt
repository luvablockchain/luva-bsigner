package com.luvapay.bsigner.fragments

import com.luvapay.bsigner.base.BaseFragment
import com.luvapay.bsigner.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountFragment : BaseFragment() {

    val vm: HomeViewModel by viewModel()

}