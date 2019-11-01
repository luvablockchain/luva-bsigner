package com.luvapay.bsigner.notification

import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.entities.TransactionInfo
import com.luvapay.bsigner.entities.TransactionInfo_
import com.onesignal.OSNotification
import com.onesignal.OneSignal
import com.orhanobut.logger.Logger
import io.objectbox.kotlin.query
import org.json.JSONObject
import java.util.*

class TransactionReceivedHandler : OneSignal.NotificationReceivedHandler {

    override fun notificationReceived(notification: OSNotification?) {
        notification?.runCatching {

            Logger.d(
                "\nnotificationReceived" +
                        "\npayload.body: \t${payload.body}" +
                        "\npayload.additionalData: \t${payload.additionalData}" +
                        "\npayload.title: \t${payload.title}" +
                        "\npayload.rawPayload: \t${payload.rawPayload}" +
                        "\npayload.body: \t${androidNotificationId}"
            )

            val xdr = payload.additionalData.getString(TransactionInfo.XDR)
            val name = payload.additionalData.getString(TransactionInfo.NAME)

            val cachedTransaction = AppBox.transactionInfoBox.query {
                equal(TransactionInfo_.envelopXdrBase64, xdr)
            }.findFirst()

            val transaction = TransactionInfo(xdr, name)
            if (cachedTransaction != null) transaction.apply { objId = cachedTransaction.objId }

            AppBox.transactionInfoBox.put(transaction)
        }
    }

}