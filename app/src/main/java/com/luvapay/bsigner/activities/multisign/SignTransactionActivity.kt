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
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.entities.Ed25519Signer_
import com.luvapay.bsigner.items.SignerSignItem
import com.luvapay.bsigner.utils.enable
import com.luvapay.bsigner.utils.prefetchText
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.orhanobut.logger.Logger
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import io.objectbox.kotlin.query
import kotlinx.coroutines.launch
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Network
import org.stellar.sdk.PaymentOperation
import org.stellar.sdk.Transaction
import kotlinx.android.synthetic.main.activity_multisign_sign_transaction.activitySignTransaction_amountTv as amountTv
import kotlinx.android.synthetic.main.activity_multisign_sign_transaction.activitySignTransaction_cancelBtn as cancelBtn
import kotlinx.android.synthetic.main.activity_multisign_sign_transaction.activitySignTransaction_fromTv as fromTv
import kotlinx.android.synthetic.main.activity_multisign_sign_transaction.activitySignTransaction_memoTv as memoTv
import kotlinx.android.synthetic.main.activity_multisign_sign_transaction.activitySignTransaction_signBtn as signBtn
import kotlinx.android.synthetic.main.activity_multisign_sign_transaction.activitySignTransaction_signerList as signerList
import kotlinx.android.synthetic.main.activity_multisign_sign_transaction.activitySignTransaction_toTv as toTv

class SignTransactionActivity : AppCompatActivity() {

    private val signerAdapter by lazy { FastItemAdapter<SignerSignItem>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multisign_sign_transaction)

        val signerKeys = intent.getStringArrayListExtra(BSIGNER_EXTRA_PUBLIC_KEYS)?.toMutableList() ?: mutableListOf()
        val transactionXdr = intent.getStringExtra(EXTRA_TRANSACTION_XDR) ?: ""

        if (signerKeys.isEmpty() || transactionXdr.isBlank()) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        //Logger.d(ed25519Signers.map { it.privateKey })

        signerList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(this@SignTransactionActivity, RecyclerView.VERTICAL, false)
            adapter = signerAdapter.apply {
                onClickListener = { _, _, _, _ ->
                    true
                }
            }
            addItemDecoration(VerticalDividerItemDecoration.Builder(this@SignTransactionActivity).build())
        }

        signerKeys.forEach {  signerKey ->
            val cachedKey = AppBox.ed25519SignerBox.query {
                equal(Ed25519Signer_.publicKey, signerKey)
            }.findFirst()
            signerAdapter.add(SignerSignItem(cachedKey?.name ?: "", signerKey, cachedKey?.privateKey ?: ""))
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

        if (signerAdapter.adapterItems.all { it.privateKey.isNotBlank() }) signBtn.enable()

        signBtn.setOnClickListener {
            Logger.d("transactionXdr: $transactionXdr")
            val transaction = Transaction.fromEnvelopeXdr(transactionXdr, Network.TESTNET)
            val publicKeys: MutableList<String> = mutableListOf()
            val signatures: MutableList<String> = mutableListOf()
            signerAdapter.adapterItems.filter { it.privateKey.isNotBlank() }.forEach { signer ->
                publicKeys.add(signer.publicKey)
                val signature = KeyPair.fromSecretSeed(signer.privateKey).signDecorated(transaction.hash()).signature.signature
                val signatureStr = Base64.encodeToString(signature, Base64.NO_WRAP)

                signatures.add(signatureStr)
            }
            val data = Intent().apply {
                putStringArrayListExtra(BSIGNER_EXTRA_PUBLIC_KEYS, ArrayList(publicKeys))
                putStringArrayListExtra(EXTRA_SIGNATURES, ArrayList(signatures))
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    companion object {
        const val BSIGNER_EXTRA_PUBLIC_KEYS = "BSIGNER_EXTRA_PUBLIC_KEYS"
        const val EXTRA_SIGNATURES = "BSIGNER_EXTRA_SIGNATURES"
        //const val EXTRA_SIGNATURE_HINTS = "BSIGNER_EXTRA_SIGNATURE_HINTS"
        const val EXTRA_TRANSACTION_XDR = "BSIGNER_EXTRA_TRANSACTION_XDR"
        //const val EXTRA_SIGNER_OBJ_IDS = "EXTRA_SIGNER_OBJ_IDS"
    }

}
