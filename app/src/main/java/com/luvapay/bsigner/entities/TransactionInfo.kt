package com.luvapay.bsigner.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.annotation.IndexType

@Entity
data class TransactionInfo(
    @Index(type = IndexType.VALUE)
    var sequenceNumber: Long = -1,
    @Index(type = IndexType.VALUE)
    var envelopXdrBase64: String = "",
    @Index(type = IndexType.VALUE)
    var submitted: Boolean = false
) {
    @Id
    var objId: Long = 0
}