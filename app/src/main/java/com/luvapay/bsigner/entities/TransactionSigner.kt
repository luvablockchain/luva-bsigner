package com.luvapay.bsigner.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class TransactionSigner(
    var key: String = "",
    var weight: Int = -1,
    var signed: Boolean = false,
    var signedAt: Long = 0
) {

    @Id
    var objId: Long = 0

    lateinit var transaction: ToOne<TransactionInfo>

    companion object {
        const val PUBLIC_KEY = "public_key"
        const val WEIGHT = "weight"
        const val SIGNED = "signed"
        const val SIGNED_AT = "signed_at"
    }
}