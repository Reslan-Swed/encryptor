package cryptography

import entity.Message
import java.security.Key
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher
import javax.crypto.SealedObject


/**
 * created by Reslan
 * lεροrεm-cοδε
 * 2019-12-05
 */

object RSA {
    private const val ALGORITHM = "RSA"

    fun encrypt(message: Message, key: Key): SealedObject {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return SealedObject(message, cipher)
    }

    fun decrypt(sealedObject: SealedObject, key: Key): Message {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)
        return sealedObject.getObject(cipher) as Message
    }

    fun encrypt(bytes: ByteArray, key: Key): ByteArray {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(bytes)
    }

    fun decrypt(bytes: ByteArray, key: Key): ByteArray {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(bytes)
    }
}