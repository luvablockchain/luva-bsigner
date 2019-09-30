package com.luvapay.bsigner.utils

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.ISelectionListener

fun <T: IItem<out RecyclerView.ViewHolder>> selectionListener(listener: (item: T, selected: Boolean) -> Unit) = object : ISelectionListener<T> {
    override fun onSelectionChanged(item: T?, selected: Boolean) {
        item?.let { listener(it, selected) }
    }
}