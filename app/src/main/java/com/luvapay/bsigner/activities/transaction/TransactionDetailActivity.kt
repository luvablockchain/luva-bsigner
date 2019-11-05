package com.luvapay.bsigner.activities.transaction

import android.os.Bundle
import android.util.Base64
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.multisign.SignTransactionActivity
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.entities.Ed25519Signer
import com.luvapay.bsigner.entities.TransactionInfo
import com.luvapay.bsigner.entities.TransactionInfo_
import com.luvapay.bsigner.entities.TransactionSigner
import com.luvapay.bsigner.items.SignatureItem
import com.luvapay.bsigner.items.TransactionItem
import com.luvapay.bsigner.server.Api
import com.luvapay.bsigner.utils.callback
import com.luvapay.bsigner.utils.prefetchText
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.onesignal.OneSignal
import com.orhanobut.logger.Logger
import io.objectbox.kotlin.query
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.anko.startActivity
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

        val cachedTransaction = AppBox.transactionInfoBox[transactionObjId]
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
        //Logger.d(cachedTransaction.signers.toMutableList())
        signatureAdapter.set(cachedTransaction.signers.map { SignatureItem(it) })



        signBtn.setOnClickListener {
            //startActivity<SignTransactionActivity>()

            val signatures = JSONArray()

            AppBox.ed25519SignerBox.all.forEach { ed25519Signer: Ed25519Signer ->
                cachedTransaction.signers.forEach { transactionSigner ->
                    if (ed25519Signer.publicKey == transactionSigner.key) {
                        val signature = JSONObject().apply {
                            put("public_key", ed25519Signer.publicKey)
                            put("signature", Base64.encodeToString(KeyPair.fromSecretSeed(ed25519Signer.privateKey).signDecorated(transaction.hash()).signature.signature, Base64.NO_WRAP))
                        }
                        signatures.put(signature)
                    }
                }
            }

            val json = JSONObject().apply {
                put("transaction_xdr", cachedTransaction.envelopXdrBase64)
                put("user_id", OneSignal.getPermissionSubscriptionState().subscriptionStatus.userId)
                put("signatures", signatures)
            }

            Logger.d("post: $json")

            val reqBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

            val req = Request.Builder()
                .url(Api.TRANSACTION_ADD_SIGNATURE)
                .post(reqBody)
                .build()

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

}
