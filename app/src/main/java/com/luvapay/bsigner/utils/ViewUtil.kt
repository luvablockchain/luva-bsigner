package com.luvapay.bsigner.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.google.android.material.button.MaterialButton

fun MaterialButton.enable() { isEnabled = true }
fun MaterialButton.disable() { isEnabled = false }

fun View.visible() { visibility = View.VISIBLE }
fun View.gone() { visibility = View.GONE }

fun EditText.textWatcher(
    beforeTextChanged: ((charSequence: CharSequence, start: Int, count: Int, after: Int) -> Unit) = {_: CharSequence, _: Int, _: Int, _: Int -> },
    afterTextChanged: ((editable: Editable)-> Unit) = { _: Editable -> },
    onTextChanged: ((charSequence: CharSequence, start: Int, count: Int, after: Int) -> Unit) = {_: CharSequence, _: Int, _: Int, _: Int -> }
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
            charSequence?.let {
                beforeTextChanged(charSequence, start, count, after)
            }
        }
        override fun afterTextChanged(editable: Editable?) {
            editable?.let { afterTextChanged(it) }
        }
        override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
            charSequence?.let { onTextChanged(charSequence, start, before, count)}
        }
    })
}

fun EditText.afterTextChanged(
    onAfterTextChanged: ((text: Editable) -> Unit)
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun afterTextChanged(editable: Editable?) {
            editable?.let { onAfterTextChanged(it) }
        }
        override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}