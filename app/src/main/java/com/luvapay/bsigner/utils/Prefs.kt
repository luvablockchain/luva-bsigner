package com.luvapay.bsigner.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.luvapay.bsigner.utils.Prefs.PREF_APP_LOCKED
import com.luvapay.bsigner.utils.Prefs.PREF_APP_PIN_KEY

object Prefs {

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
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


    const val PREF_APP_PIN_KEY = "PREF_APP_PIN_KEY"
    const val PREF_APP_LOCKED = "PREF_APP_LOCKED"
}

fun Context.initPrefs() {
    Prefs.init(this)
}

fun setAppPin(pin: String) {
    Prefs.commit { putString(PREF_APP_PIN_KEY, pin) }
}

fun getAppPin(): String = Prefs.getString(PREF_APP_PIN_KEY)

fun isAppLocked(): Boolean = Prefs.getBoolean(PREF_APP_LOCKED)

fun openAppLock() {
    Prefs.commit { putBoolean(PREF_APP_LOCKED, false) }
}

fun lockApp() {
    Prefs.commit { putBoolean(PREF_APP_LOCKED, true) }
}