package cryptography

import java.io.*
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec

/**
 * created by Reslan
 * lεροrεm-cοδε
 * 2019-12-06
 */
object RSAKeyFactory {
    private const val ALGORITHM = "RSA"
    private const val PUBLIC_KEY_FILE = "Public.key"
    private const val PRIVATE_KEY_FILE = "Private.key"

    var publicKey: PublicKey? = null
        get() {
            val pk = field ?: readPublicKey()
            field = pk
            return field
        }
    var privateKey: PrivateKey? = null
        get() {
            val pk = field ?: readPrivateKey()
            field = pk
            return field
        }

    init {
        if (privateKey == null || publicKey == null) {
            val keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM)
            keyPairGenerator.initialize(4096)
            val keyPair = keyPairGenerator.generateKeyPair()
            val keyFactory = KeyFactory.getInstance(ALGORITHM)
            val rsaPrivateKeySpec: RSAPrivateKeySpec = keyFactory.getKeySpec(
                keyPair.private,
                RSAPrivateKeySpec::class.java
            )
            val rsaPublicKeySpec: RSAPublicKeySpec = keyFactory.getKeySpec(
                keyPair.public,
                RSAPublicKeySpec::class.java
            )
            saveKey(
                PRIVATE_KEY_FILE,
                rsaPrivateKeySpec.modulus,
                rsaPrivateKeySpec.privateExponent
            )
            saveKey(
                PUBLIC_KEY_FILE,
                rsaPublicKeySpec.modulus,
                rsaPublicKeySpec.publicExponent
            )
            publicKey = keyPair.public
            privateKey = keyPair.private
        }
    }

    private fun saveKey(fileName: String, modulus: BigInteger, exponent: BigInteger) {
        FileOutputStream(fileName).use { fos ->
            ObjectOutputStream(fos).use {
                it.writeObject(modulus)
                it.writeObject(exponent)
            }
        }
    }

    private fun readPrivateKey(): PrivateKey? {
        var privateKey: PrivateKey? = null
        try {
            FileInputStream(PRIVATE_KEY_FILE).use { fis ->
                ObjectInputStream(fis).use {
                    val modulus = it.readObject() as BigInteger
                    val exponent = it.readObject() as BigInteger
                    val rsaPrivateKeySpec = RSAPrivateKeySpec(modulus, exponent)
                    val keyFactory = KeyFactory.getInstance(ALGORITHM)
                    privateKey = keyFactory.generatePrivate(rsaPrivateKeySpec)
                }
            }
        } catch (e: Exception) {

        }
        return privateKey
    }

    private fun readPublicKey(): PublicKey? {
        var publicKey: PublicKey? = null
        try {
            FileInputStream(PUBLIC_KEY_FILE).use { fis ->
                ObjectInputStream(fis).use {
                    val modulus = it.readObject() as BigInteger
                    val exponent = it.readObject() as BigInteger
                    val rsaPublicKeySpec = RSAPublicKeySpec(modulus, exponent)
                    val keyFactory = KeyFactory.getInstance(ALGORITHM)
                    publicKey = keyFactory.generatePublic(rsaPublicKeySpec)
                }
            }
        } catch (e: Exception) {

        }
        return publicKey
    }

    fun clear() {
        privateKey = null
        publicKey = null
        File(PUBLIC_KEY_FILE).delete()
        File(PRIVATE_KEY_FILE).delete()
    }

}