package com.luvapay.bsigner.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.annotation.IndexType

@Entity
data class TransactionInfo(
    @Index(type = IndexType.VALUE)
    var envelopXdrBase64: String = ""
) {
    @Id
    var objId: Long = 0
}