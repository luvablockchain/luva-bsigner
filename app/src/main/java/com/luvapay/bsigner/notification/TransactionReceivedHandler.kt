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
                "host_transaction" -> {
                    val xdr = data.getString(TransactionInfo.XDR)
                    val name = data.getString(TransactionInfo.NAME)
                    val signatures = data.getJSONArray(TransactionInfo.SIGNATURES)

                    //Logger.d("signatures: $signatures")

                    val cachedTransaction = AppBox.transactionInfoBox.query {
                        equal(TransactionInfo_.envelopXdrBase64, xdr)
                    }.findFirst()

                    val signers: MutableList<TransactionSigner> = mutableListOf()
                    for (i in 0 until signatures.length()) {
                        signers.add(
                            TransactionSigner(
                                key = signatures.getJSONObject(i).getString(TransactionSigner.PUBLIC_KEY),
                                weight = signatures.getJSONObject(i).getInt(TransactionSigner.WEIGHT),
                                signed = signatures.getJSONObject(i).getBoolean(TransactionSigner.SIGNED),
                                signedAt = signatures.getJSONObject(i).getLong(TransactionSigner.SIGNED_AT)
                            )
                        )
                    }
                    //Logger.d("signers: $signers")

                    if (cachedTransaction == null) {
                        val transaction = TransactionInfo(xdr, name)
                        transaction.signers.addAll(signers)
                        val objId = AppBox.transactionInfoBox.put(transaction)
                        Logger.d("objId: $objId")
                    } else {
                        val oldSignatures = cachedTransaction.signers

                        cachedTransaction.signers.removeAll(oldSignatures)
                        AppBox.transactionInfoBox.put(cachedTransaction)

                        AppBox.transactionSignerBox.remove(oldSignatures)

                        cachedTransaction.signers.addAll(signers)
                        val objId = AppBox.transactionInfoBox.put(cachedTransaction)
                        Logger.d("objId: $objId")
                    }
                }
                else -> {

                }
            }
        }
    }

}