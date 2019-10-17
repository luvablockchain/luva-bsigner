package com.luvapay.bsigner.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import com.orhanobut.logger.Logger
import java.util.*

fun ContextWrapper.wrap(language: String = Prefs.currentLanguage()): ContextWrapper {
    val config = baseContext.resources.configuration
    val sysLocale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.getSystemLocale()
    } else {
        this.getSystemLocaleLegacy()
    }

    if (language.isNotEmpty() && sysLocale.language != language) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.setSystemLocale(locale)
        } else {
            this.setSystemLocaleLegacy(locale)
        }
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val context = baseContext.createConfigurationContext(config)
        ContextWrapper(context)
    } else {
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        ContextWrapper(baseContext)
    }

}

@Suppress("DEPRECATION")
fun ContextWrapper.getSystemLocaleLegacy(): Locale {
    val config = baseContext.resources.configuration
    return config.locale
}

@TargetApi(Build.VERSION_CODES.N)
fun ContextWrapper.getSystemLocale(): Locale {
    val config = baseContext.resources.configuration
    return config.locales[0]
}

@Suppress("DEPRECATION")
fun ContextWrapper.setSystemLocaleLegacy(locale: Locale) {
    val config = baseContext.resources.configuration
    config.locale = locale
}

@TargetApi(Build.VERSION_CODES.N)
fun ContextWrapper.setSystemLocale(locale: Locale) {
    val config = baseContext.resources.configuration
    config.setLocale(locale)
}