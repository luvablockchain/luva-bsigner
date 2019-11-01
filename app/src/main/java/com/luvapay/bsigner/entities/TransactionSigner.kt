package com.luvapay.bsigner.entities

import com.luvapay.bsigner.E25519_PUBLIC_KEY
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class TransactionSigner(
    var key: String = "",
    var weight: Int = -1,
    var type: String = E25519_PUBLIC_KEY
) {

    @Id
    var objId: Long = 0

    lateinit var transaction: ToOne<TransactionInfo>
}