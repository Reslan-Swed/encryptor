package entity

import java.io.Serializable

class SignedMessage(val message: Message, val signature: ByteArray) : Serializable {
    companion object {
        private const val serialVersionUID = 521561242549862L
    }
}