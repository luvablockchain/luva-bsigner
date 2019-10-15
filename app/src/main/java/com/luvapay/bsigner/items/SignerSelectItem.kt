package com.luvapay.bsigner.items

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import com.luvapay.bsigner.R
import com.luvapay.bsigner.entities.Ed25519Signer
import com.luvapay.bsigner.utils.getColorCompat
import com.luvapay.bsigner.utils.prefetchText
import com.luvapay.bsigner.utils.visible
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.item_signer_select.view.itemAccountSelect_nameTv as nameTv
import kotlinx.android.synthetic.main.item_signer_select.view.itemAccountSelect_publicKeyTv as publicKeyTv
import kotlinx.android.synthetic.main.item_signer_select.view.itemAccountSelect_container as container
import kotlinx.android.synthetic.main.item_signer_select.view.itemAccountSelect_iconImg as iconImg

data class SignerSelectItem(val account: Ed25519Signer) : AbstractItem<SignerSelectItem.ViewHolder>() {

    override var identifier: Long = account.objId
    override val layoutRes: Int = R.layout.item_signer_select
    override val type: Int = 0

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(itemView: View) : FastAdapter.ViewHolder<SignerSelectItem>(itemView) {

        override fun bindView(item: SignerSelectItem, payloads: MutableList<Any>) {
            itemView.apply {
                nameTv.apply {
                    item.account.name.takeIf { it.isNotBlank() }?.let {
                        visible()
                        val fgColor = if (item.isSelected) Color.parseColor("#FFFFFF") else Color.parseColor("#000000")
                        setTextColor(fgColor)
                        prefetchText(it)

                        itemView.publicKeyTv.maxLines = 1
                        itemView.publicKeyTv.ellipsize = TextUtils.TruncateAt.END
                    }
                }
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
                    val color = if (item.isSelected) context.getColorCompat(R.color.colorPrimary) else Color.parseColor("#606060")
                    setColorFilter(color)
                    val icon = if (item.isSelected) context.getDrawable(R.drawable.ic_check) else context.getDrawable(R.drawable.ic_key)
                    setImageDrawable(icon)
                }
            }
        }

        override fun unbindView(item: SignerSelectItem) {
            itemView.apply {
                publicKeyTv.text = null
            }
        }
    }

}