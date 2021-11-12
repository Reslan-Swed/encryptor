package client

import com.jfoenix.controls.*
import com.jfoenix.validation.DoubleValidator
import com.jfoenix.validation.NumberValidator
import com.jfoenix.validation.RequiredFieldValidator
import cryptography.AES
import entity.Exchange
import entity.Message
import cryptography.RSA
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.net.URL
import java.security.PublicKey
import java.util.*
import kotlin.random.Random


/**
 * created by Reslan
 * lεροrεm-cοδε
 * 2019-12-09
 */

class ClientPanel : Initializable {

    @FXML
    private lateinit var pane: AnchorPane

    @FXML
    private lateinit var senderNumberField: JFXTextField

    @FXML
    private lateinit var receiverNameField: JFXTextField

    @FXML
    private lateinit var exchangeAmountField: JFXTextField

    @FXML
    private lateinit var noteField: JFXTextArea

    @FXML
    private lateinit var sendButton: JFXButton

    @FXML
    private lateinit var spinner: JFXSpinner

    private var socket: Socket? = null
    private var outputStream: ObjectOutputStream? = null
    private var inputStream: ObjectInputStream? = null
    private var status = SimpleBooleanProperty(false)
    private var publicKey: PublicKey? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        println("init")
        spinner.visibleProperty().bind(!sendButton.visibleProperty())
        senderNumberField.disableProperty().bind(!sendButton.visibleProperty())
        receiverNameField.disableProperty().bind(!sendButton.visibleProperty())
        exchangeAmountField.disableProperty().bind(!sendButton.visibleProperty())
        noteField.disableProperty().bind(!sendButton.visibleProperty())
        val requiredValidator = RequiredFieldValidator()
        requiredValidator.message = "This field is required !"
        val numberValidator = NumberValidator()
        numberValidator.message = "Only numbers are allowed !"
        val doubleValidator = DoubleValidator()
        doubleValidator.message = "Please check amount value !"
        senderNumberField.validators.add(numberValidator)
        receiverNameField.validators.add(requiredValidator)
        exchangeAmountField.validators.add(doubleValidator)
        noteField.validators.add(requiredValidator)
        status.addListener { _, _, newValue ->
            if (!newValue) {
                connect()
            }
        }
        connect()
    }

    @FXML
    fun onSendClick(event: ActionEvent) {
        if (senderNumberField.validate() && receiverNameField.validate() && exchangeAmountField.validate() && noteField.validate() && isConnected()) {
            val senderName = senderNumberField.text.toInt()
            val receiverNumber = receiverNameField.text
            val amount = exchangeAmountField.text.toDouble()
            val note = noteField.text
            val exchange = Exchange(senderName, receiverNumber, amount, note)
            send(exchange)
        }
    }

    private fun connect() {
        object : AsyncTask<Unit, Unit, Unit>() {
            lateinit var alert: JFXAlert<Any>
            override fun onPreExecute() {
                println("Trying to connect")
                alert = createAlert(pane.scene.window as Stage, "Trying to connect...", JFXSpinner())
                alert.show()
            }

            override fun doInBackground(vararg params: Unit?): Unit {
                while (!isConnected()) {
                    try {
                        socket = Socket("localhost", 8088)
                        outputStream = ObjectOutputStream(socket!!.getOutputStream())
                        outputStream!!.flush()
                        inputStream = ObjectInputStream(socket!!.getInputStream())
                        publicKey = inputStream!!.readObject() as PublicKey
                        status.set(true)
                        println(socket!!.localPort)
                    } catch (e: Exception) {
                    }
                }
            }

            override fun onPostExecute(params: Unit?) {
                println("Connected")
                alert.hideWithAnimation()
            }
        }.setDaemon(false).execute()
    }

    private fun disConnect() {
        try {
            outputStream?.writeObject(null)
            outputStream?.close()
            inputStream?.close()
            socket?.close()
        } catch (e: Exception) {
        }
        status.set(false)
    }

    private fun isConnected() = status.get()

    private fun send(exchange: Exchange) {
        object : AsyncTask<Exchange, Unit, String>() {
            override fun onPreExecute() {
                sendButton.isVisible = false
            }

            override fun doInBackground(vararg params: Exchange?): String {
                return try {
                    val key = generateRandomKey(socket!!.localPort)
                    val message = Message(AES.encrypt(params[0]!!, key), key)
                    val encryptedMessage = RSA.encrypt(message, publicKey!!)
                    outputStream!!.writeObject(encryptedMessage)
                    inputStream!!.readObject() as String
                } catch (e: Exception) {
                    e.printStackTrace()
                    disConnect()
                    "Operation Failed !"
                }
            }

            override fun onPostExecute(params: String?) {
                println(params)
                createAlert(pane.scene.window as Stage, "Info", Label(params), "Ok").show()
                sendButton.isVisible = true
            }
        }.setDaemon(false).execute(exchange)
    }

    private fun generateRandomKey(): ByteArray {
        return Random.nextBytes(16)
    }

    private fun generateRandomKey(seed: Int): ByteArray {
        val random = Random(seed)
        return random.nextBytes(16)
    }

}
