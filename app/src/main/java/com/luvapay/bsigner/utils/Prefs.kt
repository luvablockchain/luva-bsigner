package com.luvapay.bsigner.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

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

    fun edit(commit: Boolean = false, block: SharedPreferences.Editor.() -> Unit) {
        prefs.edit(commit) {
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

}

fun Context.initPrefs() {
    Prefs.init(this)
}

fun setAppPin(pin: String) {
    Prefs.commit { putString("APP_PIN", pin) }
}