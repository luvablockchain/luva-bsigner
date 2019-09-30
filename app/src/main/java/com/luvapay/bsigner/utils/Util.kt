package com.luvapay.bsigner.utils

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat

infix fun Boolean.then(block: () -> Unit): Boolean {
    if (this) block()
    return this
}

infix fun Boolean.otherwise(block: () -> Unit): Boolean {
    if (!this) block()
    return this
}

infix fun Any?.ifNull(block: () -> Unit): Any? {
    if (this == null) block()
    return this
}

infix fun Any?.notNull(block: () -> Unit): Any? {
    if (this != null) block()
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