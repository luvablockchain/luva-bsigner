package com.luvapay.bsigner.fragments

import com.luvapay.bsigner.base.BaseFragment
import com.luvapay.bsigner.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TransactionFragment : BaseFragment() {

    val vm: HomeViewModel by sharedViewModel()



}