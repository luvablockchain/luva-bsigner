package com.luvapay.bsigner.notification

import com.onesignal.OSNotification
import com.onesignal.OneSignal
import com.orhanobut.logger.Logger

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

        }
    }

}