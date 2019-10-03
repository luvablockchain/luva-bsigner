package com.luvapay.bsigner.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.sonhvp.kryptographer.Kryptographer
import com.sonhvp.kryptographer.key.CryptographicKey
import java.lang.Exception

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

infix fun String.toQrCode(size: Int): Bitmap? = try {
    MultiFormatWriter().encode(this, BarcodeFormat.QR_CODE, size, size)?.toQrCodeBitmap()
} catch (e: Exception) {
    e.printStackTrace()
    null
}

fun BitMatrix.toQrCodeBitmap(): Bitmap = this.run {
    val pixels = IntArray(width * height)
    for (h in 0 until height) {
        val offSet = h * width
        for (w in 0 until width) {
            pixels[offSet + w] = if (get(w,h)) Color.BLACK else Color.WHITE
        }
    }
    return@run Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        setPixels(pixels, 0, width, 0,0, width, height)
    }
}