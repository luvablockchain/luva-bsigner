package com.luvapay.bsigner.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.transaction.TransactionDetailActivity
import com.luvapay.bsigner.base.BaseFragment
import com.luvapay.bsigner.entities.TransactionInfo
import com.luvapay.bsigner.entities.TransactionInfo_
import com.luvapay.bsigner.items.TransactionItem
import com.luvapay.bsigner.server.Api
import com.luvapay.bsigner.unSubscribe
import com.luvapay.bsigner.utils.callback
import com.luvapay.bsigner.viewmodel.HomeViewModel
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.orhanobut.logger.Logger
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.query
import io.objectbox.reactive.DataSubscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.anko.startActivity
import org.json.JSONArray
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlinx.android.synthetic.main.fragment_home_transaction.view.fragmentHomeTransaction_transactionList as transactionList
import kotlinx.android.synthetic.main.fragment_home_transaction.view.fragmentHomeTransaction_refreshBtn as refreshBtn

class TransactionFragment : BaseFragment() {

    override val layout: Int = R.layout.fragment_home_transaction
    private val vm: HomeViewModel by sharedViewModel()

    private lateinit var transactionAdapter: FastItemAdapter<TransactionItem>
    private lateinit var transactionSub: DataSubscription

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionAdapter = FastItemAdapter()
        transactionAdapter.onClickListener = { _, _, item, _ ->
            context?.startActivity<TransactionDetailActivity>(
                "objId" to item.transactionInfo.objId
                /*TransactionInfo.XDR to item.transactionInfo.envelopXdrBase64,
                TransactionInfo.NAME to item.transactionInfo.name*/
            )
            true
        }

        view.transactionList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = transactionAdapter
        }

        view.refreshBtn.setOnClickListener { getTransaction() }
    }

    override fun onResume() {
        super.onResume()
        transactionSub = AppBox.transactionInfoBox.query {  }.subscribe().on(AndroidScheduler.mainThread()).onError {
            //Error

        }.observer { transactionInfoList ->
            lifecycleScope.launch {
                val items = withContext(Dispatchers.Default) {
                    return@withContext transactionInfoList.map { TransactionItem(it) }
                }
                transactionAdapter.set(items)
                //Logger.d(transactionInfoList)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        transactionSub.unSubscribe()
    }

    private fun getTransaction() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val signers = AppBox.ed25519SignerBox.all
                if (signers.isEmpty()) return@withContext

                val signerKeys = JSONArray()
                signers.forEach { signerKeys.put(it.publicKey) }

                val json = JSONObject().apply {
                    put("signer_keys", signerKeys)
                }

                Logger.d("post: $json")

                val reqBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

                val req = Request.Builder()
                    .url(Api.GET_TRANSACTIONS)
                    .post(reqBody)
                    .build()

                OkHttpClient().newCall(req).enqueue(callback(
                    response = { _, response ->
                        val body = JSONObject(response.body?.string() ?: "")
                        Logger.d("body: $body")

                        val transactions = body.getJSONObject("data").getJSONArray("transactions")
                        Logger.d("transactions: $transactions")

                        for (i in 0 until transactions.length()) {
                            val transactionXdr = transactions.getJSONObject(i).getString("transaction_xdr")
                            val transactionName = transactions.getJSONObject(i).getString("transaction_name")
                            val signerKeys = transactions.getJSONObject(i).getJSONArray("signatures")
                            val cachedTransactionInfo = AppBox.transactionInfoBox.query {
                                equal(TransactionInfo_.envelopXdrBase64, transactionXdr)
                            }.findFirst()

                            val transaction = TransactionInfo(transactionXdr)
                            /*for (j in 0 until signerKeys.length()) {
                                transaction.signers.add()
                                signerKeys.getString(j)
                            }*/
                            Logger.d(transaction)

                            if (cachedTransactionInfo != null) {
                                //AppBox.transactionSignerBox.remove(cachedTransactionInfo.signers)
                                transaction.objId = cachedTransactionInfo.objId
                            }
                            AppBox.transactionInfoBox.put(transaction)
                        }
                    },
                    failure = { _, e ->
                        e.printStackTrace()
                    }
                ))
            }
        }
    }

    companion object {
        const val TAG = "transactionFragment"
    }

}