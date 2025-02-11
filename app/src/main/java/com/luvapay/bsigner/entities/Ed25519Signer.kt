package com.luvapay.bsigner.entities

import com.luvapay.bsigner.utils.decrypt
import com.luvapay.bsigner.utils.encrypt
import com.orhanobut.logger.Logger
import io.objectbox.annotation.*
import io.objectbox.converter.PropertyConverter
import org.stellar.sdk.KeyPair

@Entity
data class Ed25519Signer(
    var name: String = "",
    @Unique
    @Index(type = IndexType.VALUE)
    var publicKey: String = "",
    @Unique
    @Index(type = IndexType.VALUE)
    //@Convert(converter = SecurityConverter::class, dbType = String::class)
    var privateKey: String = "",
    //@Convert(converter = SecurityConverter::class, dbType = String::class)
    var mnemonic: String = "",
    @Index(type = IndexType.VALUE)
    var subscribed: Boolean = false,
    @Index(type = IndexType.VALUE)
    var deleted: Boolean = false
) {

    @Id
    var objId: Long = 0

    fun toKeyPair(): KeyPair = KeyPair.fromSecretSeed(privateKey)

    companion object {

        const val OBJ_ID = "OBJ_ID"

        class SecurityConverter : PropertyConverter<String, String> {
            override fun convertToDatabaseValue(entityProperty: String?): String {
                Logger.d(entityProperty)
                return (entityProperty ?: "").encrypt()
            }

            override fun convertToEntityProperty(databaseValue: String?): String {
                Logger.d("\n$databaseValue\n${(databaseValue ?: "").decrypt()}")
                return (databaseValue ?: "").decrypt()
            }
        }

    }

}