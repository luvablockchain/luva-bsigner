package com.luvapay.bsigner.entities

import io.objectbox.annotation.*
import java.security.PrivateKey

@Entity
data class StellarAccount(
    @Unique
    @Index(type = IndexType.VALUE)
    var publicKey: String,
    @Unique
    var privateKey: String,
    var mnemonic: String
) {
    @Id
    var objId: Long = 0

    fun toKeyPair() {

    }
}