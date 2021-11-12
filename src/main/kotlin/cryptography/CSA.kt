package cryptography

import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.pkcs.PKCS10CertificationRequest
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.security.PrivateKey
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*


object CSA {
    fun sign(
        csr: PKCS10CertificationRequest,
        caPrivate: PrivateKey,
        issuerName: String,
        dayValidity: Int = 1
    ): X509Certificate {
        Security.addProvider(BouncyCastleProvider())

        val issuer = X500Name("CN=$issuerName")
        val serial = BigInteger.valueOf(System.currentTimeMillis())
        val from = Date()
        val to = Date(System.currentTimeMillis() + dayValidity * 86400000L)

        val myCertificateGenerator = X509v3CertificateBuilder(
            issuer, serial, from, to, csr.subject, csr.subjectPublicKeyInfo
        )

        val signatureAlgorithm = "SHA256WithRSA"

        val contentSigner = JcaContentSignerBuilder(signatureAlgorithm)
            .build(caPrivate)

        val holder = myCertificateGenerator.build(contentSigner)
        val certificate = holder.toASN1Structure()

        return ByteArrayInputStream(certificate.encoded).use {
            val cf = CertificateFactory.getInstance("X.509", "BC")
            cf.generateCertificate(it) as X509Certificate
        }
    }

    fun readCertificate(fileName: String): X509Certificate {
        val fact = CertificateFactory.getInstance("X.509")
        return FileInputStream(fileName).use {
            fact.generateCertificate(it) as X509Certificate
        }
    }

    fun storeCertificate(fileName: String, certificate: X509Certificate) {
        File(fileName).writer().use {
            JcaPEMWriter(it).use { pem ->
                pem.writeObject(certificate)
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val csr = CSR.generate(
            "Leporem Code",
            "Programming",
            "",
            "",
            "California",
            "US",
            RSAKeyFactory.privateKey!!,
            RSAKeyFactory.publicKey!!
        )
        println(CSR.printPem(csr))
//        sign(csr,RSAKeyFactory.privateKey!!,4)
//        println(selfSign(RSAKeyFactory.privateKey!!, RSAKeyFactory.publicKey!!, "RESLAN"))
        val signed = sign(csr, RSAKeyFactory.privateKey!!, "Reslan")
//        println(signed)
        storeCertificate("cert.pem", signed)
        val readed = readCertificate("cert.pem")
//        RSAKeyFactory.clear()
        println(readed)
        readed.verify(RSAKeyFactory.publicKey!!)
//        println(signed.verify(RSAKeyFactory.publicKey!!))
//        println(signed.checkValidity( Date(System.currentTimeMillis() -  86400000L)))
    }
}