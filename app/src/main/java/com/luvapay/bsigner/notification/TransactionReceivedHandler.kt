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

            val transactionXdr = payload.additionalData.getString("transactionXdr")
            val cachedTransaction = AppBox.transactionInfoBox.query {
                equal(TransactionInfo_.envelopXdrBase64, transactionXdr)
            }.findFirst()
            AppBox.transactionInfoBox.put(
                cachedTransaction?.apply { envelopXdrBase64 = transactionXdr } ?: TransactionInfo(transactionXdr)
            )
        }
    }

}