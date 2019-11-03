package com.luvapay.bsigner.items

import android.view.View
import com.luvapay.bsigner.R
import com.luvapay.bsigner.entities.TransactionSigner
import com.luvapay.bsigner.utils.prefetchText
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.item_signature.view.itemSignature_publicKeyTv as publicKeyTv

data class SignatureItem(val transactionSigner: TransactionSigner) : AbstractItem<SignatureItem.ViewHolder>() {

    override val layoutRes: Int = R.layout.item_transaction
    override val type: Int = 0

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(itemView: View) : FastAdapter.ViewHolder<SignatureItem>(itemView) {

        override fun bindView(item: SignatureItem, payloads: MutableList<Any>) {
            itemView.publicKeyTv prefetchText item.transactionSigner.key
        }

        override fun unbindView(item: SignatureItem) {
        }

    }

}