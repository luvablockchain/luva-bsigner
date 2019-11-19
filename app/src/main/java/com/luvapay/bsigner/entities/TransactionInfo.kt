package com.luvapay.bsigner.entities

import io.objectbox.annotation.*
import io.objectbox.relation.ToMany

@Entity
data class TransactionInfo(
    @Index(type = IndexType.VALUE)
    var envelopXdrBase64: String = "",
    var name: String = "",
    var hostedAt: Long = 0
) {
    @Id
    var objId: Long = 0

    @Backlink(to = "transaction")
    lateinit var signers: ToMany<TransactionSigner>

    companion object {
        const val XDR = "xdr"
        const val NAME = "name"
        const val HOSTED_AT = "hosted_at"
        const val WEIGHT = "weight"
        const val SIGNATURES = "signatures"
    }

}