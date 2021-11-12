package server

import database.DAO
import java.net.ServerSocket
import java.util.concurrent.Executors

/**
 * created by Reslan
 * lεροrεm-cοδε
 * 2019-12-06
 */
object Server {
    fun run() {
        try {
            ServerSocket(8088).use {
                val pool = Executors.newCachedThreadPool()
                DAO.init()
                println("Starting Server")
                while (true) {
                    val clientSocket = it.accept()
                    println(clientSocket.port)
                    pool.execute(ClientHandler(clientSocket))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun main() {
    Server.run()
}