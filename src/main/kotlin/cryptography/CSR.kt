package cryptography

import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.pkcs.PKCS10CertificationRequest
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.security.PrivateKey
import java.security.PublicKey
import javax.security.auth.x500.X500Principal

object CSR {
    fun generate(
        CN: String, OU: String, O: String,
        L: String, ST: String, C: String,
        privateKey: PrivateKey,
        publicKey: PublicKey
    ): PKCS10CertificationRequest {
        val subject =
            X500Principal("C=$C, ST=$ST, L=$L, O=$O, OU=$OU, CN=$CN")
        val signGen = JcaContentSignerBuilder("SHA1withRSA").build(privateKey)
        val builder = JcaPKCS10CertificationRequestBuilder(subject, publicKey)
        return builder.build(signGen)
    }

    fun printPem(request: Any): String {
        return ByteArrayOutputStream().use {
            OutputStreamWriter(it).use { streamWriter ->
                JcaPEMWriter(streamWriter).use { pem ->
                    pem.writeObject(request)
                }
            }
            String(it.toByteArray())
        }
    }

}

