package cryptography

import entity.Message
import entity.SignedMessage
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom


object DigitalSignature {
    fun sign(message: Message, privateKey: PrivateKey): SignedMessage {
        val hashCode = oneWayHash(message)
        val signature = RSA.encrypt(hashCode, privateKey)
        return SignedMessage(message, signature)
    }

    fun verify(signedMessage: SignedMessage, publicKey: PublicKey): Boolean {
        val signature = signedMessage.signature
        val message = signedMessage.message
        val hashCode = RSA.decrypt(signature, publicKey)
        return hashCode.contentEquals(oneWayHash(message))
    }

    private fun oneWayHash(message: Message): ByteArray {
        val bytes = ByteArrayOutputStream().use {
            ObjectOutputStream(it).use { objectOutputStream ->
                objectOutputStream.writeObject(message)
            }
            it.toByteArray()
        }
        val digest = MessageDigest.getInstance("MD5")
        digest.update(getSalt())
        return digest.digest(bytes)
    }

    private fun getSalt(): ByteArray {
        val sr = SecureRandom.getInstance("SHA1PRNG")
        val salt = ByteArray(16)
        sr.nextBytes(salt)
        return salt
    }
}