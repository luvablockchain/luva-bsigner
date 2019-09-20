package com.luvapay.bsigner.ui

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class MnemonicListView (ctx: Context, attrs: AttributeSet) : RecyclerView(ctx, attrs) {

    init {
        itemAnimator = DefaultItemAnimator()
        layoutManager = FlexboxLayoutManager(ctx).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
    }

}