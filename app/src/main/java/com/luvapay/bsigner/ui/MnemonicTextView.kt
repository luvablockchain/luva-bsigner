package com.luvapay.bsigner.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView

@SuppressLint("ViewConstructor")
class MnemonicTextView(mnemonic: String, ctx: Context) : AppCompatTextView(ctx) {

    init {
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            setMargins(5,5,5,5)
        }
        text = mnemonic
    }

}