package com.luvapay.bsigner.entities

import io.objectbox.annotation.*
import org.stellar.sdk.KeyPair
import java.security.PrivateKey

@Entity
data class StellarAccount(
    var name: String = "",
    @Unique
    @Index(type = IndexType.VALUE)
    var publicKey: String,
    @Unique
    @Index(type = IndexType.VALUE)
    var privateKey: String,
    @Index(type = IndexType.VALUE)
    var mnemonic: String
) {
    @Id
    var objId: Long = 0

    fun toKeyPair(): KeyPair = KeyPair.fromSecretSeed(privateKey)

    companion object {
        const val OBJ_ID = "OBJ_ID"
    }

}