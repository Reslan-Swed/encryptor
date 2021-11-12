package client

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * created by Reslan
 * lεροrεm-cοδε
 * 2019-12-09
 */

class Main : Application() {

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        val root = FXMLLoader.load<Parent>(javaClass.getResource("/client_panel.fxml"))
        primaryStage.title = "Exchange Transfer"
        primaryStage.scene = Scene(root)
        primaryStage.scene.stylesheets.add(javaClass.getResource("/styles.css").toExternalForm())
        primaryStage.isResizable = false
        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }
}
