package com.luvapay.bsigner.notification

import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.entities.TransactionInfo
import com.luvapay.bsigner.entities.TransactionInfo_
import com.luvapay.bsigner.entities.TransactionSigner
import com.onesignal.OSNotification
import com.onesignal.OneSignal
import com.orhanobut.logger.Logger
import io.objectbox.kotlin.query
import org.json.JSONObject
import java.util.*

class TransactionReceivedHandler : OneSignal.NotificationReceivedHandler {

    override fun notificationReceived(notification: OSNotification?) {
        notification?.runCatching {

            /*Logger.d(
                "\nnotificationReceived" +
                        "\npayload.body: \t${payload.body}" +
                        "\npayload.additionalData: \t${payload.additionalData}" +
                        "\npayload.title: \t${payload.title}" +
                        "\npayload.rawPayload: \t${payload.rawPayload}" +
                        "\npayload.body: \t${androidNotificationId}"
            )*/
            //Logger.d("\npayload.additionalData: \t${payload.additionalData}")

            val notificationData = JSONObject(payload.additionalData.toString())

            val type = notificationData.optString("type")
            val data = notificationData.optJSONObject("data") ?: JSONObject()

            Logger.d(type)
            Logger.d(data)

            when (type) {
                "host_transaction", "sign_transaction" -> {
                    AppBox.addTransaction(data)
                }
                else -> { }
            }
        }
    }

}