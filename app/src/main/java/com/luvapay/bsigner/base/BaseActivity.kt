package com.luvapay.bsigner.base

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.coroutineScope
import com.luvapay.bsigner.model.InitData
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class BaseActivity : AppCompatActivity() {

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Logger.d(newConfig.locale)
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    fun Toolbar.init() {
        setSupportActionBar(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.setNavigationOnClickListener { onBackPressed() }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    open fun init(initData: InitData) {

    }
}