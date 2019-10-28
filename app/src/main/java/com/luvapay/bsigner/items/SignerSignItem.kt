package com.luvapay.bsigner.items

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import com.luvapay.bsigner.R
import com.luvapay.bsigner.utils.prefetchText
import com.luvapay.bsigner.utils.visible
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.item_signer_sign.view.*

data class SignerSignItem(val name: String, val publicKey: String, val privateKey: String) : AbstractItem<SignerSignItem.ViewHolder>() {

    override val layoutRes: Int = R.layout.item_signer_sign
    override val type: Int = 0

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(itemView: View) : FastAdapter.ViewHolder<SignerSignItem>(itemView) {

        override fun bindView(item: SignerSignItem, payloads: MutableList<Any>) {
            if (item.name.isNotBlank()) {
                itemView.itemSignerSign_nameTv.apply {
                    visible()
                    prefetchText(item.name)
                    itemView.itemSignerSign_publicKeyTv.maxLines = 1
                    itemView.itemSignerSign_publicKeyTv.ellipsize = TextUtils.TruncateAt.END
                }
            }

            if (item.publicKey.isNotBlank()) {
                itemView.itemSignerSign_publicKeyTv prefetchText item.publicKey
            }

            if (item.privateKey.isBlank()) {
                itemView.itemSignerSign_publicKeyTv.setTextColor(Color.parseColor("#CCCCCC"))
            }
        }

        override fun unbindView(item: SignerSignItem) {
            itemView.itemSignerSign_nameTv.text = null
            itemView.itemSignerSign_publicKeyTv.text = null
        }

    }

}