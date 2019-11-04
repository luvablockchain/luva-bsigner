package com.luvapay.bsigner.entities

import io.objectbox.annotation.*
import io.objectbox.relation.ToMany

@Entity
data class TransactionInfo(
    @Index(type = IndexType.VALUE)
    var envelopXdrBase64: String = "",
    var name: String = ""
) {
    @Id
    var objId: Long = 0

    @Backlink(to = "transaction")
    lateinit var signers: ToMany<TransactionSigner>

    companion object {
        const val XDR = "transaction_xdr"
        const val NAME = "transaction_name"
        const val SIGNATURES = "signatures"
    }

}