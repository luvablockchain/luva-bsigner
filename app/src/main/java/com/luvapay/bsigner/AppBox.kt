package com.luvapay.bsigner

import android.app.Application
import com.luvapay.bsigner.entities.*
import com.orhanobut.logger.Logger
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.objectbox.reactive.DataSubscription
import org.json.JSONObject
import org.stellar.sdk.KeyPair

object AppBox {

    lateinit var ed25519SignerBox: Box<Ed25519Signer>
    lateinit var transactionInfoBox: Box<TransactionInfo>
    lateinit var transactionSignerBox: Box<TransactionSigner>

    fun init(boxStore: BoxStore) {
        boxStore.run {
            ed25519SignerBox = boxFor()
            transactionInfoBox = boxFor()
            transactionSignerBox = boxFor()
        }
    }

    fun addAccount(mnemonics: String, accountNumber: Int = 0, accountAdded: () -> Unit, accountExists: () -> Unit, error: (throwable: Throwable) -> Unit) {
        runCatching {
            val keyPair = KeyPair.fromBip39Seed(createBip39Seed(mnemonics.toCharArray()), accountNumber)
            val addedAccount = ed25519SignerBox.query {
                equal(
                    Ed25519Signer_.publicKey, keyPair.accountId
                )
            }.findFirst()
            return@runCatching if (addedAccount == null) {
                val account = Ed25519Signer(
                    publicKey = keyPair.accountId,
                    privateKey = String(keyPair.secretSeed),
                    mnemonic = mnemonics
                )
                ed25519SignerBox.put(
                    account
                )
                Result.success(true)
            } else {
                Result.success(false)
            }
        }.onSuccess {
            when (it.getOrThrow()) {
                true -> accountAdded()
                false -> accountExists()
            }
        }.onFailure {
            it.printStackTrace()
            error(it)
        }
    }

    fun addTransaction(transactionObj: JSONObject) {
        val xdr = transactionObj.getString(TransactionInfo.XDR)
        val name = transactionObj.getString(TransactionInfo.NAME)
        val signatures = transactionObj.getJSONArray(TransactionInfo.SIGNATURES)

        //Logger.d("signatures: $signatures")

        val cachedTransaction = transactionInfoBox.query {
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
            val objId = transactionInfoBox.put(transaction)
            Logger.d("objId: $objId")
        } else {
            val oldSignatures = cachedTransaction.signers
            //Remove all current cached signature in transaction
            cachedTransaction.signers.removeAll(oldSignatures)
            transactionInfoBox.put(cachedTransaction)
            ////Remove all current cached signature
            transactionSignerBox.remove(oldSignatures)
            //Add new update signature to transaction
            cachedTransaction.signers.addAll(signers)
            transactionInfoBox.put(cachedTransaction)
            //Logger.d("objId: $objId")
        }
    }
}

fun Application.initAppBox() {
    AppBox.init(MyObjectBox.builder().androidContext(this@initAppBox).build())
}

fun DataSubscription.unSubscribe() { this.takeUnless { it.isCanceled }?.run { cancel() } }