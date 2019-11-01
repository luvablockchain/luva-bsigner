package com.luvapay.bsigner

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jakewharton.threetenabp.AndroidThreeTen
import com.luvapay.bsigner.notification.TransactionOpenedHandler
import com.luvapay.bsigner.notification.TransactionReceivedHandler
import com.luvapay.bsigner.utils.*
import com.luvapay.bsigner.viewmodel.HomeViewModel
import com.luvapay.bsigner.workers.AppLockWorker
import com.onesignal.OneSignal
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.sonhvp.kryptographer.Kryptographer
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.security.Security

class App : Application(), LifecycleObserver {

    private val workManager by lazy { WorkManager.getInstance(this@App) }

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this@App)
        //Logger
        initLogger()
        //Init prefs
        initPrefs()
        //Threeten
        AndroidThreeTen.init(this@App)
        //
        Kryptographer.initWithDefaultKeys(this@App)
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

        if (getAppPin().isNotBlank()) lockApp()

        /**
         * BouncyCastle for JDK lower than 1.8
         */
        Security.removeProvider("BC")
        Security.insertProviderAt(BouncyCastleProvider(), 1)

        /**
         * OneSignal Initialization
         */
        OneSignal.startInit(this@App)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(false)
            .autoPromptLocation(false)
            .setNotificationReceivedHandler(TransactionReceivedHandler())
            .setNotificationOpenedHandler(TransactionOpenedHandler(this@App))
            .init()
        //OneSignal.sendTags()
        //OneSignal.getPermissionSubscriptionState().subscriptionStatus.userId
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onMoveToForeground() {
        workManager.cancelUniqueWork(AppLockWorker.UNIQUE_WORK_NAME)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onMoveToBackground() {
        val appLockWorker = OneTimeWorkRequestBuilder<AppLockWorker>().build()
        workManager.enqueueUniqueWork(AppLockWorker.UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, appLockWorker)
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