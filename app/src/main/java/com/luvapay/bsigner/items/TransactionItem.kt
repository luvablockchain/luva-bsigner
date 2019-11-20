package com.luvapay.bsigner.items

import android.view.View
import com.luvapay.bsigner.R
import com.luvapay.bsigner.entities.TransactionInfo
import com.luvapay.bsigner.utils.prefetchText
import com.luvapay.bsigner.utils.toDateTimeStr
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import org.stellar.sdk.Network
import org.stellar.sdk.Transaction
import kotlinx.android.synthetic.main.item_transaction.view.itemTransaction_sourceTv as sourceTv
import kotlinx.android.synthetic.main.item_transaction.view.itemTransaction_timeTv as timeTv

data class TransactionItem(val transactionInfo: TransactionInfo) : AbstractItem<TransactionItem.ViewHolder>() {

    override var identifier: Long = transactionInfo.objId
    override val layoutRes: Int = R.layout.item_transaction
    override val type: Int = 0

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(itemView: View) : FastAdapter.ViewHolder<TransactionItem>(itemView) {

        override fun bindView(item: TransactionItem, payloads: MutableList<Any>) {
            itemView.sourceTv prefetchText item.transactionInfo.name //(Transaction.fromEnvelopeXdr(item.transactionInfo.envelopXdrBase64, Network.TESTNET).sourceAccount)
            itemView.timeTv prefetchText item.transactionInfo.hostedAt.toDateTimeStr()
        }

        override fun unbindView(item: TransactionItem) {
            itemView.sourceTv.text = null
        }

    }

}