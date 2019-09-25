package com.luvapay.bsigner

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.luvapay.bsigner.utils.initPrefs
import com.luvapay.bsigner.viewmodel.HomeViewModel
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

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

        startKoin {
            androidContext(this@App)
            modules(
                module {
                    viewModel { HomeViewModel(get()) }
                }
            )
        }
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