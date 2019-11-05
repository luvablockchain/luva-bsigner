package com.luvapay.bsigner.items

import android.view.View
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.entities.TransactionSigner
import com.luvapay.bsigner.obj.AppObj
import com.luvapay.bsigner.utils.prefetchText
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.item_signature.view.itemSignature_publicKeyTv as publicKeyTv
import kotlinx.android.synthetic.main.item_signature.view.itemSignature_statusTv as statusTv

data class SignatureItem(val transactionSigner: TransactionSigner) : AbstractItem<SignatureItem.ViewHolder>() {

    override var identifier: Long = transactionSigner.objId
    override val layoutRes: Int = R.layout.item_signature
    override val type: Int = 0

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(itemView: View) : FastAdapter.ViewHolder<SignatureItem>(itemView) {

        override fun bindView(item: SignatureItem, payloads: MutableList<Any>) {
            itemView.publicKeyTv prefetchText item.transactionSigner.key

            if (item.transactionSigner.signed) {
                itemView.statusTv.prefetchText(itemView.context.getString(R.string.transaction_signature_signed))
                itemView.statusTv.setTextColor(AppObj.transactionSignedColor)
            } else {
                itemView.statusTv.prefetchText(itemView.context.getString(R.string.transaction_signature_waiting))
                itemView.statusTv.setTextColor(AppObj.transactionWaitingColor)
            }
        }

        override fun unbindView(item: SignatureItem) {
            itemView.publicKeyTv.text = null
        }

    }

}