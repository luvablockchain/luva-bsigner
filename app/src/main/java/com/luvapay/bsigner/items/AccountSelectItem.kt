package com.luvapay.bsigner.items

import android.graphics.Color
import android.view.View
import com.luvapay.bsigner.R
import com.luvapay.bsigner.entities.StellarAccount
import com.luvapay.bsigner.utils.getColorCompat
import com.luvapay.bsigner.utils.prefetchText
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.item_account_select.view.itemAccountSelect_publicKeyTv as publicKeyTv
import kotlinx.android.synthetic.main.item_account_select.view.itemAccountSelect_container as container
import kotlinx.android.synthetic.main.item_account_select.view.itemAccountSelect_iconImg as iconImg

data class AccountSelectItem(val account: StellarAccount) : AbstractItem<AccountSelectItem.ViewHolder>() {

    override var identifier: Long = account.objId
    override val layoutRes: Int = R.layout.item_account_select
    override val type: Int = 0

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(itemView: View) : FastAdapter.ViewHolder<AccountSelectItem>(itemView) {

        override fun bindView(item: AccountSelectItem, payloads: MutableList<Any>) {
            itemView.apply {
                publicKeyTv.apply {
                    val fgColor = if (item.isSelected) Color.parseColor("#FFFFFF") else Color.parseColor("#000000")
                    setTextColor(fgColor)
                    prefetchText(item.account.publicKey)
                }
                container.apply {
                    val bgColor = if (item.isSelected) itemView.context.getColorCompat(R.color.colorPrimary) else Color.parseColor("#FFFFFF")
                    setCardBackgroundColor(bgColor)
                }
                iconImg.apply {
                    val color = if (item.isSelected) Color.parseColor("#FFFFFF") else Color.parseColor("#606060")
                    setColorFilter(color)
                }
            }
        }

        override fun unbindView(item: AccountSelectItem) {
            itemView.apply {
                publicKeyTv.text = null
            }
        }
    }

}