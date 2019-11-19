package com.luvapay.bsigner.activities.transaction

import android.os.Bundle
import android.util.Base64
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.entities.Ed25519Signer
import com.luvapay.bsigner.entities.Ed25519Signer_
import com.luvapay.bsigner.entities.TransactionInfo_
import com.luvapay.bsigner.items.SignatureItem
import com.luvapay.bsigner.server.Api
import com.luvapay.bsigner.utils.*
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.onesignal.OneSignal
import com.orhanobut.logger.Logger
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.query
import io.objectbox.reactive.DataSubscription
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Network
import org.stellar.sdk.PaymentOperation
import org.stellar.sdk.Transaction
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_toolbar as toolbar
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_amountTv as amountTv
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_fromTv as fromTv
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_memoTv as memoTv
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_signBtn as signBtn
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_signatureList as signatureList
import kotlinx.android.synthetic.main.activity_transaction_detail.activityTransactionDetail_toTv as toTv

class TransactionDetailActivity : BaseActivity() {

    private lateinit var signatureAdapter: FastItemAdapter<SignatureItem>

    private lateinit var signerSub: DataSubscription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_detail)

        toolbar.init()

        val transactionObjId = intent.getLongExtra("objId", -2)

        if (transactionObjId <= 0) {
            Logger.d("transactionXdr is blank")
            finish()
            return
        }

        signerSub = AppBox.transactionInfoBox.query {
            equal(TransactionInfo_.objId, transactionObjId)
        }.subscribe()
            .onError {
                Logger.d(it)
            }
            .on(AndroidScheduler.mainThread())
            .observer { transactions ->
                val cachedTransaction = transactions.first()
                val transaction = Transaction.fromEnvelopeXdr(cachedTransaction.envelopXdrBase64, Network.TESTNET)

                fromTv prefetchText transaction.sourceAccount
                kotlin.runCatching {
                    transaction.operations.firstOrNull()?.let { operation ->
                        val transactionOperation = operation as PaymentOperation
                        fromTv prefetchText transaction.sourceAccount
                        toTv prefetchText transactionOperation.destination
                        amountTv prefetchText transactionOperation.amount
                    }
                    memoTv prefetchText (transaction.memo.toString())
                }

                signatureAdapter = FastItemAdapter()
                signatureList.apply {
                    itemAnimator = DefaultItemAnimator()
                    layoutManager = GridLayoutManager(
                        this@TransactionDetailActivity,
                        if (cachedTransaction.signers.size >=3) 2 else 1,
                        RecyclerView.HORIZONTAL,
                        false
                    )
                    adapter = signatureAdapter
                }
                Logger.d(cachedTransaction.signers.toMutableList())
                signatureAdapter.set(cachedTransaction.signers.map { SignatureItem(it) })

                Logger.d(cachedTransaction.signers.map { it.signed })


                val availableKeys = AppBox.ed25519SignerBox.query {
                    `in`(Ed25519Signer_.publicKey, cachedTransaction.signers.filter { !it.signed }.map { it.key }.toTypedArray())
                }.find()

                if (availableKeys.isEmpty()) signBtn.disable() else signBtn.enable()

                signBtn.setOnClickListener {
                    //startActivity<SignTransactionActivity>()

                    val signatures = JSONArray()

                    availableKeys.forEach { availableKey ->
                        val signatureBase64 = Base64.encodeToString(
                            KeyPair.fromSecretSeed(availableKey.privateKey).signDecorated(transaction.hash()).signature.signature,
                            Base64.NO_WRAP
                        )
                        val signature = JSONObject().apply {
                            put("public_key", availableKey.publicKey)
                            put("signature", signatureBase64)
                        }
                        signatures.put(signature)
                    }

                    val reqBody = JSONObject().apply {
                        put("xdr", cachedTransaction.envelopXdrBase64)
                        put("user_id", OneSignal.getPermissionSubscriptionState().subscriptionStatus.userId)
                        put("signatures", signatures)
                    }

                    val req = request {
                        url(Api.SIGN_TRANSACTION)
                        post(reqBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                    }

                    OkHttpClient().newCall(req).enqueue(callback(
                        response = { _, response ->
                            val body = JSONObject(response.body?.string() ?: "")
                            Logger.d("body: $body")
                        },
                        failure = { _, e ->
                            e.printStackTrace()
                        }
                    ))
                }
            }

        /*val cachedTransaction = AppBox.transactionInfoBox[transactionObjId]
        val transaction = Transaction.fromEnvelopeXdr(cachedTransaction.envelopXdrBase64, Network.TESTNET)

        fromTv prefetchText transaction.sourceAccount
        kotlin.runCatching {
            transaction.operations.firstOrNull()?.let { operation ->
                val transactionOperation = operation as PaymentOperation
                fromTv prefetchText transaction.sourceAccount
                toTv prefetchText transactionOperation.destination
                amountTv prefetchText transactionOperation.amount
            }
            memoTv prefetchText (transaction.memo.toString())
        }

        signatureAdapter = FastItemAdapter()
        signatureList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = GridLayoutManager(
                this@TransactionDetailActivity,
                if (cachedTransaction.signers.size >=3) 2 else 1,
                RecyclerView.HORIZONTAL,
                false
            )
            adapter = signatureAdapter
        }
        Logger.d(cachedTransaction.signers.toMutableList())
        signatureAdapter.set(cachedTransaction.signers.map { SignatureItem(it) })

        Logger.d(cachedTransaction.signers.map { it.signed })


        val availableKeys = AppBox.ed25519SignerBox.query {
            `in`(Ed25519Signer_.publicKey, cachedTransaction.signers.filter { !it.signed }.map { it.key }.toTypedArray())
        }.find()

        if (availableKeys.isEmpty()) signBtn.disable() else signBtn.enable()

        signBtn.setOnClickListener {
            //startActivity<SignTransactionActivity>()

            val signatures = JSONArray()

            availableKeys.forEach { availableKey ->
                val signatureBase64 = Base64.encodeToString(
                    KeyPair.fromSecretSeed(availableKey.privateKey).signDecorated(transaction.hash()).signature.signature,
                    Base64.NO_WRAP
                )
                val signature = JSONObject().apply {
                    put("public_key", availableKey.publicKey)
                    put("signature", signatureBase64)
                }
                signatures.put(signature)
            }

            val reqBody = JSONObject().apply {
                put("xdr", cachedTransaction.envelopXdrBase64)
                put("user_id", OneSignal.getPermissionSubscriptionState().subscriptionStatus.userId)
                put("signatures", signatures)
            }

            val req = request {
                url(Api.SIGN_TRANSACTION)
                post(reqBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
            }

            OkHttpClient().newCall(req).enqueue(callback(
                response = { _, response ->
                    val body = JSONObject(response.body?.string() ?: "")
                    Logger.d("body: $body")
                },
                failure = { _, e ->
                    e.printStackTrace()
                }
            ))
        }*/
    }

}
