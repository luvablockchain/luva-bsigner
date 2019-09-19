package com.luvapay.bsigner.items

import android.view.View
import com.luvapay.bsigner.R
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.item_mnemonic.view.*

data class MenemonicItem(private val mnemonic: String) : AbstractItem<MenemonicItem.ViewHolder>() {

    override val type: Int = 0
    override val layoutRes: Int = R.layout.item_mnemonic

    override fun getViewHolder(v: View): ViewHolder =
        ViewHolder(v)

    class ViewHolder(private val view: View) : FastAdapter.ViewHolder<MenemonicItem>(view) {

        override fun bindView(item: MenemonicItem, payloads: MutableList<Any>) {
            view.apply {
                itemMnemonic_tv.text = item.mnemonic
            }
        }

        override fun unbindView(item: MenemonicItem) {
            view.apply {
                itemMnemonic_tv.text = null
            }
        }

    }

}