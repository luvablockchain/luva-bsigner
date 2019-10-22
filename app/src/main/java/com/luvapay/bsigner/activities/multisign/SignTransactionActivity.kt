package com.luvapay.bsigner.activities.multisign

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luvapay.bsigner.*
import com.luvapay.bsigner.items.SignerItem
import com.luvapay.bsigner.utils.prefetchText
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.orhanobut.logger.Logger
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.stellar.sdk.*
import kotlinx.android.synthetic.main.activity_multisign_sign_transaction.activitySignTransaction_fromTv as fromTv
import kotlinx.android.synthetic.main.activity_multisign_sign_transaction.activitySignTransaction_toTv as toTv
import kotlinx.android.synthetic.main.activity_multisign_sign_transaction.activitySignTransaction_memoTv as memoTv
import kotlinx.android.synthetic.main.activity_multisign_sign_transaction.activitySignTransaction_amountTv as amountTv
import kotlinx.android.synthetic.main.activity_multisign_sign_transaction.activitySignTransaction_cancelBtn as cancelBtn
import kotlinx.android.synthetic.main.activity_multisign_sign_transaction.activitySignTransaction_signBtn as signBtn
import kotlinx.android.synthetic.main.activity_multisign_sign_transaction.activitySignTransaction_signerList as signerList

class SignTransactionActivity : AppCompatActivity() {

    private val signerAdapter by lazy { FastItemAdapter<SignerItem>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multisign_sign_transaction)

        if (intent == null) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        val signerObjIds = intent.getLongArrayExtra(PickSignerActivity.EXTRA_SIGNER_OBJ_IDS)?.toMutableList() ?: mutableListOf()
        val transactionXdr = intent.getStringExtra(PickSignerActivity.EXTRA_TRANSACTION_XDR) ?: ""

        if (signerObjIds.isEmpty() || transactionXdr.isBlank()) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        val ed25519Signers = AppBox.ed25519SignerBox.get(signerObjIds).toMutableList()

        Logger.d(ed25519Signers.map { it.privateKey })

        signerList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(this@SignTransactionActivity, RecyclerView.VERTICAL, false)
            adapter = signerAdapter.apply {
                onClickListener = { _, _, item, _ ->
                    true
                }
                set(
                    ed25519Signers.map {
                        SignerItem(it).apply {
                            canModify = false
                            card = false
                        }
                    }
                )
            }
            addItemDecoration(VerticalDividerItemDecoration.Builder(this@SignTransactionActivity).build())
            //addItemDecoration(DividerItemDecoration(this@SignTransactionActivity, DividerItemDecoration.VERTICAL))
        }

        lifecycleScope.launch {
            val transaction = Transaction.fromEnvelopeXdr(transactionXdr, Network.TESTNET)
            transaction.operations.firstOrNull()?.let { operation ->
                val transactionOperation = operation as PaymentOperation
                fromTv prefetchText transaction.sourceAccount
                toTv prefetchText transactionOperation.destination
                amountTv prefetchText transactionOperation.amount
            }
            memoTv prefetchText (transaction.memo.toString())
        }

        cancelBtn.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        signBtn.setOnClickListener {
            Logger.d("transactionXdr: $transactionXdr")
            val transaction = Transaction.fromEnvelopeXdr(transactionXdr, Network.TESTNET)
            val publicKeys: MutableList<String> = mutableListOf()
            val signatures: MutableList<String> = mutableListOf()
            signerAdapter.adapterItems.map { it.account }.forEach { signer ->
                publicKeys.add(signer.publicKey)
                val signature = KeyPair.fromSecretSeed(signer.privateKey).signDecorated(transaction.hash()).signature.signature
                val signatureStr = Base64.encodeToString(signature, Base64.NO_WRAP)

                signatures.add(signatureStr)
            }
            val data = Intent().apply {
                putStringArrayListExtra(PickSignerActivity.BSIGNER_EXTRA_PUBLIC_KEYS, ArrayList(publicKeys))
                putStringArrayListExtra(PickSignerActivity.EXTRA_SIGNATURES, ArrayList(signatures))
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

}
