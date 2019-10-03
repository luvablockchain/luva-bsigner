package com.luvapay.bsigner.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import com.sonhvp.kryptographer.Kryptographer
import com.sonhvp.kryptographer.key.CryptographicKey

infix fun Boolean.then(block: () -> Unit): Boolean {
    if (this) block()
    return this
}

infix fun Boolean.otherwise(block: () -> Unit): Boolean {
    if (!this) block()
    return this
}

infix fun AppCompatTextView.prefetchText(charSequence: CharSequence) {
    setTextFuture(
        PrecomputedTextCompat.getTextFuture(
            charSequence,
            TextViewCompat.getTextMetricsParams(this),
            null
        )
    )
}

infix fun Context.getColorCompat(colorId: Int) = ContextCompat.getColor(this, colorId)

fun Context.copyToClipBoard(label: String, text: String) {
    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText(label, text))
}

fun Context.showPopupMenu(anchor: View, menuId: Int, onItemClick: (menuItem: MenuItem) -> Unit) {
    PopupMenu(this@showPopupMenu, anchor).apply {
        menuInflater.inflate(menuId, menu)
        setOnMenuItemClickListener { menuItem ->
            onItemClick.invoke(menuItem)
            true
        }
    }.show()
}

fun getCryptKey(): CryptographicKey = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    Kryptographer.defaultSymmetricKey()
} else {
    Kryptographer.defaultAsymmetricKey()
}

fun String.encrypt(): String = getCryptKey().encrypt(this)
fun String.decrypt(): String = getCryptKey().decrypt(this)