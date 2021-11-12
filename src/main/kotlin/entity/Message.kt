package entity

import java.io.Serializable
import javax.crypto.SealedObject

/**
 * created by Reslan
 * lεροrεm-cοδε
 * 2019-12-08
 */
class Message(val encryptedExchange: SealedObject, val key: ByteArray) : Serializable {
    companion object {
        private const val serialVersionUID = 521561242549861L
    }
}