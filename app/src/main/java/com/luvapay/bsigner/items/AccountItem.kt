package com.luvapay.bsigner.items

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import com.luvapay.bsigner.R
import com.luvapay.bsigner.entities.StellarAccount
import com.luvapay.bsigner.utils.getColorCompat
import com.luvapay.bsigner.utils.prefetchText
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.item_account.view.*

data class AccountItem(val account: StellarAccount) : AbstractItem<AccountItem.ViewHolder>() {

    override var identifier: Long = account.objId
    override val layoutRes: Int = R.layout.item_account
    override val type: Int = 0

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(itemView: View) : FastAdapter.ViewHolder<AccountItem>(itemView) {

        override fun bindView(item: AccountItem, payloads: MutableList<Any>) {
            itemView.apply {
                itemAccount_publicKey.apply {
                    val fgColor = if (item.isSelected) Color.parseColor("#FFFFFF") else Color.parseColor("#000000")
                    itemAccount_publicKey.setTextColor(fgColor)
                    prefetchText(item.account.publicKey)
                }
                itemAccount_container.apply {
                    val bgColor = if (item.isSelected) itemView.context.getColorCompat(R.color.colorPrimary) else Color.parseColor("#FFFFFF")
                    setCardBackgroundColor(bgColor)
                }
                itemAccount_icon.apply {
                    val color = if (item.isSelected) Color.parseColor("#FFFFFF") else Color.parseColor("#606060")
                    setColorFilter(color)
                }
            }
        }

        override fun unbindView(item: AccountItem) {
            itemView.apply {
                itemAccount_publicKey.text = null
            }
        }
    }

}