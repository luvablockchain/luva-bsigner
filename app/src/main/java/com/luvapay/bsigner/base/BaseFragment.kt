package com.luvapay.bsigner.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseFragment : Fragment() {

    abstract val layout: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
            = inflater.inflate(layout, container, false)

    override fun onResume() {
        super.onResume()
        //EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        //EventBus.getDefault().unregister(this)
    }

}