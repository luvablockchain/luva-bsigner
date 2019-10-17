package com.luvapay.bsigner.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

object LocaleManager {

    fun updateResources(context: Context, language: String = Prefs.currentLanguage()): Context {
        var context = context

        val locale = Locale(language)
        Locale.setDefault(locale)

        val res = context.resources
        val config = Configuration(res.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            context = context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
        return context
    }

}