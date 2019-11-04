package com.luvapay.bsigner.notification

import android.content.Context
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.activities.transaction.TransactionDetailActivity
import com.luvapay.bsigner.entities.TransactionInfo
import com.luvapay.bsigner.entities.TransactionInfo_
import com.luvapay.bsigner.entities.TransactionSigner
import com.luvapay.bsigner.entities.TransactionSigner_
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal
import com.orhanobut.logger.Logger
import io.objectbox.kotlin.query
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class TransactionOpenedHandler(val context: Context) : OneSignal.NotificationOpenedHandler {

    override fun notificationOpened(result: OSNotificationOpenResult?) {
        result?.run {
            notification?.runCatching {

                val notificationData = JSONObject(payload.additionalData.toString())
                val type = notificationData.optString("type")
                val data = notificationData.optJSONObject("data") ?: JSONObject()

                /*Logger.d(type)
                Logger.d(data)*/

                when (type) {
                    "sign_transaction" -> {
                        val xdr = data.getString(TransactionInfo.XDR)

                        val cachedTransaction = AppBox.transactionInfoBox.query {
                            equal(TransactionInfo_.envelopXdrBase64, xdr)
                        }.findFirst()

                        Logger.d(cachedTransaction)

                        cachedTransaction?.let {
                            context.startActivity<TransactionDetailActivity>("objId" to it.objId)
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

}