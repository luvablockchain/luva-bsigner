package com.luvapay.multisignature

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.luvapay.multisignature.utils.initPrefs
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.parse.Parse
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
        //EventBus index
        EventBus.builder().addIndex(MyEventBusIndex()).installDefaultEventBus()

        Parse.initialize(this)
    }

    private fun initLogger() {
        //Logger
        val logFormat = PrettyFormatStrategy.newBuilder()
            .tag("App")
            .showThreadInfo(false)
            .build()
        Logger.clearLogAdapters()
        Logger.addLogAdapter(object: AndroidLogAdapter(logFormat) {
            override fun isLoggable(priority: Int, tag: String?): Boolean = BuildConfig.DEBUG
        })
    }

}