package com.luvapay.bsigner

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.luvapay.bsigner.utils.initPrefs
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import org.greenrobot.eventbus.EventBus

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //Logger
        initLogger()
        //Init prefs
        initPrefs()
        //Threeten
        AndroidThreeTen.init(this@App)
        //
        initAppBox()
        //EventBus index
        EventBus.builder().addIndex(MyEventBusIndex()).installDefaultEventBus()
    }

    private fun initLogger() {
        //Logger
        val logFormat = PrettyFormatStrategy.newBuilder()
            .tag("bsigner")
            .showThreadInfo(false)
            .build()
        Logger.clearLogAdapters()
        Logger.addLogAdapter(object: AndroidLogAdapter(logFormat) {
            override fun isLoggable(priority: Int, tag: String?): Boolean = BuildConfig.DEBUG
        })
    }

}