package com.luvapay.bsigner.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseFragment
import com.luvapay.bsigner.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TransactionFragment : BaseFragment() {

    private val vm: HomeViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_transaction, container, false)
    }

}