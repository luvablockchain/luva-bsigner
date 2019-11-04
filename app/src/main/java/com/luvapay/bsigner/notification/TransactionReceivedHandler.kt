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
                "sign_transaction" -> {
                    val xdr = data.getString(TransactionInfo.XDR)
                    val name = data.getString(TransactionInfo.NAME)
                    val signatures = data.getJSONArray(TransactionInfo.SIGNATURES)

                    val cachedTransaction = AppBox.transactionInfoBox.query {
                        equal(TransactionInfo_.envelopXdrBase64, xdr)
                    }.findFirst()

                    val transaction = TransactionInfo(xdr, name)
                    if (cachedTransaction != null) transaction.apply { objId = cachedTransaction.objId }

                    val signer: MutableList<TransactionSigner> = mutableListOf()
                    for (i in 0 until signatures.length()) {
                        signer.add(
                            TransactionSigner(
                                key = signatures.getJSONObject(i).getString("public_key"),
                                signed = signatures.getJSONObject(i).getString("signature").isNotBlank()
                            )
                        )
                    }

                    transaction.signers.removeAll(transaction.signers)
                    transaction.signers.addAll(signer)
                    val objId = AppBox.transactionInfoBox.put(transaction)
                    Logger.d("objId: $objId")
                }
                else -> {

                }
            }
            Logger.d("adasd")
        }
    }

}