package com.luvapay.bsigner.notification

import com.onesignal.OSNotification
import com.onesignal.OneSignal
import com.orhanobut.logger.Logger

class TransactionReceivedHandler : OneSignal.NotificationReceivedHandler {

    override fun notificationReceived(notification: OSNotification?) {
        notification?.runCatching {

            Logger.d(
                "notificationReceived" +
                        "payload.body: \t${payload.body}" +
                        "payload.additionalData: \t${payload.additionalData}" +
                        "payload.title: \t${payload.title}" +
                        "payload.rawPayload: \t${payload.rawPayload}" +
                        "payload.body: \t${androidNotificationId}"
            )

        }
    }

}