package com.luvapay.bsigner.base

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.coroutineScope
import com.luvapay.bsigner.model.InitData
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {

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

    fun scopedLaunch(block: CoroutineScope.() -> Unit) {
        lifecycle.coroutineScope.launch {
            block()
        }
    }

    fun bgCoroutine(block: () -> Unit) {
        lifecycle.coroutineScope.launch {
            withContext(Dispatchers.Default) { block() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

}