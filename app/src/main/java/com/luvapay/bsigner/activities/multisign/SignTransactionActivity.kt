package com.luvapay.bsigner.activities.multisign

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.luvapay.bsigner.*
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.stellar.sdk.KeyPair

class SignTransactionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multisign_sign_transaction)

        if (intent == null) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        val signerObjIds = intent.getLongArrayExtra(PickSignerActivity.EXTRA_SIGNER_OBJ_IDS)?.toMutableList() ?: mutableListOf()
        val transactionXdr = intent.getStringExtra(PickSignerActivity.EXTRA_TRANSACTION_XDR) ?: "change later"

        if (signerObjIds.isEmpty() || transactionXdr == null) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        val ed25519Signers = AppBox.ed25519SignerBox.get(signerObjIds).toMutableList()

        Logger.d(ed25519Signers.map { it.privateKey })

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val sourceAccount = Horizon.server.accounts().account("GDICGWXEFFJJKGBOH7LL45PPPA6ZFVHEG3PCXP4BUAJHAA6FIFIVG4LJ")

                val transaction = testnetTransaction(sourceAccount) {
                    addOperation(
                        nativePaymentOperation(
                            "GC53FZQZFQ6J5ZABVQDZKEQWU42P3SK4Y7RNOKZ3JKVCCNAKMSE6S2CW",
                            "10"
                        )
                    )
                    .setTimeout(300)
                }

                //Receive xdr convert to Transaction Object and hash
                val transactionTxHash = transaction.hash()

                val signatures = arrayListOf<String>()
                val signatureHints = arrayListOf<String>()

                ed25519Signers.forEach { ed25519Signer ->
                    val keyPair = KeyPair.fromSecretSeed(ed25519Signer.privateKey)

                    val signatureDecorated = keyPair.signDecorated(transactionTxHash)
                    signatures.add(String(signatureDecorated.signature.signature))
                    signatureHints.add(String(signatureDecorated.hint.signatureHint))
                }

                Logger.d(signatures)
                Logger.d(signatureHints)


                try {
                    //Logger.d(Horizon.server.submitTransaction(transaction).isSuccess)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}
