package com.luvapay.bsigner.items

import android.view.View
import com.luvapay.bsigner.R
import com.luvapay.bsigner.utils.prefetchText
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import org.stellar.sdk.Transaction
import kotlinx.android.synthetic.main.item_transaction.view.itemTransaction_sourceTv as sourceTv

data class TransactionItem(override var identifier: Long, private val transaction: Transaction) : AbstractItem<TransactionItem.ViewHolder>() {

    override val layoutRes: Int = R.layout.item_transaction
    override val type: Int = 0

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(itemView: View) : FastAdapter.ViewHolder<TransactionItem>(itemView) {

        override fun bindView(item: TransactionItem, payloads: MutableList<Any>) {
            itemView.sourceTv prefetchText item.transaction.sourceAccount
        }

        override fun unbindView(item: TransactionItem) {
            itemView.sourceTv.text = null
        }

    }

}