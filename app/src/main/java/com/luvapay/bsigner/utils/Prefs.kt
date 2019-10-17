package com.luvapay.bsigner.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.luvapay.bsigner.utils.Prefs.APP_LOCKED
import com.luvapay.bsigner.utils.Prefs.APP_PIN_KEY

object Prefs {

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        if (!::prefs.isInitialized) prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun commit(block: SharedPreferences.Editor.() -> Unit) {
        prefs.edit(true) {
            this.apply(block)
        }
    }

    fun apply(block: SharedPreferences.Editor.() -> Unit) {
        prefs.edit {
            this.apply(block)
        }
    }

    fun getStringSet(key: String, fallback: MutableSet<String> = mutableSetOf()): MutableSet<String>
            = prefs.getStringSet(key, fallback) ?: fallback
    fun getString(key: String, fallback: String = ""): String = prefs.getString(key, fallback) ?: fallback
    fun getBoolean(key: String, fallback: Boolean = false): Boolean = prefs.getBoolean(key, fallback)
    fun getInt(key: String, fallback: Int = -1): Int = prefs.getInt(key, fallback)
    fun getFloat(key: String, fallback: Float = -1f): Float = prefs.getFloat(key, fallback)
    fun getLong(key: String, fallback: Long = -1): Long = prefs.getLong(key, fallback)

    fun changeLanguageToVn() {
        apply { putString(LANGUAGE, "vi") }
    }

    fun changeLanguageToEn() {
        apply { putString(LANGUAGE, "en") }
    }

    fun currentLanguage(): String = getString(LANGUAGE, "vi")

    fun currentLanguage(
        vn : () -> Unit,
        en : () -> Unit
    ) {
        when (getString(LANGUAGE, "vi")) {
            "vi" -> vn()
            "en" -> en()
        }
    }

    const val APP_PIN_KEY = "APP_PIN_KEY"
    const val APP_LOCKED = "APP_LOCKED"
    private const val LANGUAGE = "pref_language"
}

fun Context.initPrefs() {
    Prefs.init(this)
}

fun setAppPin(pin: String) {
    Prefs.commit { putString(APP_PIN_KEY, pin) }
}

fun getAppPin(): String = Prefs.getString(APP_PIN_KEY)

fun isAppLocked(): Boolean = Prefs.getBoolean(APP_LOCKED)

fun openAppLock() {
    Prefs.commit { putBoolean(APP_LOCKED, false) }
}

fun lockApp() {
    Prefs.commit { putBoolean(APP_LOCKED, true) }
}

sealed class Language {
    object Vn : Language()
    object En : Language()
}