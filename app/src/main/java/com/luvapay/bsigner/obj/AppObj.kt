package com.luvapay.bsigner.obj

import android.content.Context
import android.graphics.Color
import com.luvapay.bsigner.R
import com.luvapay.bsigner.utils.getColorCompat

object AppObj {

    var transactionSignedColor: Int = 0
    var transactionWaitingColor: Int = 0

    fun init(context: Context) {
        transactionSignedColor = context.getColorCompat(R.color.colorPrimary)
        transactionWaitingColor = Color.parseColor("#727272")
    }

}

fun Context.initAppObj() { AppObj.init(this) }