package com.luvapay.bsigner.items

import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.entities.StellarAccount
import com.luvapay.bsigner.utils.invisible
import com.luvapay.bsigner.utils.prefetchText
import com.luvapay.bsigner.utils.visible
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.item_account.view.itemAccount_nameTv as nameTv
import kotlinx.android.synthetic.main.item_account.view.itemAccount_publicKeyTv as publicKeyTv
import kotlinx.android.synthetic.main.item_account.view.itemAccount_editBtn as editBtn
import kotlinx.android.synthetic.main.item_account.view.itemAccount_removeBtn as removeBtn

data class AccountItem(val account: StellarAccount) : AbstractItem<AccountItem.ViewHolder>() {

    override var identifier: Long = account.objId
    override val layoutRes: Int = R.layout.item_account
    override val type: Int = 0

    var canModify = false

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(itemView: View) : FastAdapter.ViewHolder<AccountItem>(itemView) {

        override fun bindView(item: AccountItem, payloads: MutableList<Any>) {
            itemView.nameTv prefetchText item.account.name
            itemView.publicKeyTv prefetchText item.account.publicKey

            if (item.canModify) {
                itemView.editBtn.visible()
                itemView.removeBtn.visible()
            } else {
                itemView.editBtn.invisible()
                itemView.removeBtn.invisible()
            }

            itemView.editBtn.setOnClickListener {
                MaterialDialog(itemView.context).show {
                    title(R.string.enter_account_name)
                    input{ _, text ->
                        AppBox.accountBox.put(item.account.apply { name = text.toString() })
                    }
                    positiveButton(R.string.ok)
                    negativeButton(R.string.cancel)
                }
            }

            itemView.removeBtn.setOnClickListener {
                MaterialDialog(itemView.context).show {
                    message(R.string.warning_delete)
                    positiveButton(R.string.ok) {
                        AppBox.accountBox.remove(item.account)
                    }
                    negativeButton(R.string.cancel)
                }
            }
        }

        override fun unbindView(item: AccountItem) {
            itemView.nameTv.text = null
            itemView.publicKeyTv.text = null
        }

    }

}