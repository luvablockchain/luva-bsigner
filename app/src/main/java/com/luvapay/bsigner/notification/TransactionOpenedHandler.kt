package com.luvapay.bsigner.notification

import android.content.Context
import com.luvapay.bsigner.activities.transaction.TransactionDetailActivity
import com.luvapay.bsigner.entities.TransactionInfo
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal
import com.orhanobut.logger.Logger
import org.jetbrains.anko.startActivity

class TransactionOpenedHandler(val context: Context) : OneSignal.NotificationOpenedHandler {

    override fun notificationOpened(result: OSNotificationOpenResult?) {
        result?.run {
            notification?.runCatching {

                Logger.d(
                    "\nnotificationOpened" +
                            "\npayload.body: \t${payload.body}" +
                            "\npayload.additionalData: \t${payload.additionalData}" +
                            "\npayload.title: \t${payload.title}" +
                            "\npayload.rawPayload: \t${payload.rawPayload}" +
                            "\npayload.body: \t${androidNotificationId}"
                )

                val xdr = payload.additionalData.getString(TransactionInfo.XDR)
                val name = payload.additionalData.getString(TransactionInfo.NAME)

                context.startActivity<TransactionDetailActivity>(
                    TransactionInfo.XDR to xdr,
                    TransactionInfo.NAME to name
                )
            }
        }
    }

}