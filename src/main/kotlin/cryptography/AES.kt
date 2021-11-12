package cryptography

import entity.Exchange
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.SealedObject
import javax.crypto.spec.SecretKeySpec


/**
 * created by Reslan
 * lεροrεm-cοδε
 * 2019-12-05
 */

object AES {
    private const val ALGORITHM = "AES";

    fun encrypt(exchange: Exchange, key: ByteArray) = encrypt(exchange, generateKey(key))

    fun encrypt(exchange: Exchange, key: Key): SealedObject {
        val cipher: Cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return SealedObject(exchange, cipher)
    }

    fun decrypt(sealedObject: SealedObject, key: ByteArray) = decrypt(sealedObject, generateKey(key))

    fun decrypt(sealedObject: SealedObject, key: Key): Exchange {
        val cipher: Cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)
        return sealedObject.getObject(cipher) as Exchange
    }

    private fun generateKey(key: ByteArray): Key = SecretKeySpec(key, ALGORITHM)

}