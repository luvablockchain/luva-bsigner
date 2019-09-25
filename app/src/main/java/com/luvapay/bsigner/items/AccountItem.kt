package com.luvapay.bsigner.items

import android.view.View
import com.luvapay.bsigner.R
import com.luvapay.bsigner.entities.StellarAccount
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

data class AccountItem(val account: StellarAccount) : AbstractItem<AccountItem.ViewHolder>() {

    override var identifier: Long = account.objId
    override val layoutRes: Int = R.layout.item_account
    override val type: Int = 0

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(itemView: View) : FastAdapter.ViewHolder<AccountItem>(itemView) {
        override fun bindView(item: AccountItem, payloads: MutableList<Any>) {

        }

        override fun unbindView(item: AccountItem) {
        }

    }
}