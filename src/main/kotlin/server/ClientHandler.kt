package server

import cryptography.AES
import cryptography.RSA
import cryptography.RSAKeyFactory
import database.DAO
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.net.SocketException
import javax.crypto.SealedObject
import kotlin.random.Random

class ClientHandler(private val socket: Socket) : Runnable {

    override fun run() {
        try {
            socket.use {
                ObjectInputStream(it.getInputStream()).use { inputStream ->
                    ObjectOutputStream(it.getOutputStream()).use { outputStream ->

                        outputStream.writeObject(RSAKeyFactory.publicKey)
                        var encryptedMessage = inputStream.readObject()

                        while (encryptedMessage != null) {
                            val decryptedMessage =
                                RSA.decrypt(encryptedMessage as SealedObject, RSAKeyFactory.privateKey!!)
                            val exchangeMessage =
                                AES.decrypt(decryptedMessage.encryptedExchange, generateRandomKey(it.port))

                            //do some logic
                            val senderAccount = DAO.getAccount(exchangeMessage.senderId)
                            if (senderAccount == null) {
                                outputStream.writeObject("Your Account not found!")
                            } else {
                                val receiverAccount = DAO.getAccount(exchangeMessage.receiverName)
                                if (receiverAccount == null) {
                                    outputStream.writeObject("Receiver Account not found!")
                                } else {
                                    if (senderAccount.balance < exchangeMessage.amount) {
                                        outputStream.writeObject("Your balance is not enough! you have ${senderAccount.balance.format(2)}$ only")
                                    } else {
                                        senderAccount.balance -= exchangeMessage.amount
                                        receiverAccount.balance += exchangeMessage.amount
                                        DAO.updateAccount(senderAccount)
                                        DAO.updateAccount(receiverAccount)
                                        DAO.addExchange(receiverAccount.id, exchangeMessage)
                                        outputStream.writeObject("Operation Done Successfully")
                                    }
                                }
                            }

                            println(exchangeMessage)

                            encryptedMessage = inputStream.readObject()
                        }

                    }
                }
            }
        } catch (e: SocketException) {
            println("client out")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun generateRandomKey(seed: Int): ByteArray {
        val random = Random(seed)
        return random.nextBytes(16)
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)

}
