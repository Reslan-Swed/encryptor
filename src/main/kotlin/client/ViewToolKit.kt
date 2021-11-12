package client

import com.jfoenix.animation.alert.JFXAlertAnimation
import com.jfoenix.controls.JFXAlert
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialogLayout
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.stage.Modality
import javafx.stage.Stage

/**
 * created by Reslan
 * lεροrεm-cοδε
 * 08/11/2019
 */

fun createAlert(
    stage: Stage,
    heading: String,
    body: Node,
    positiveButton: String? = null,
    onPositiveButtonClick: () -> Unit = {},
    negativeButton: String? = null,
    onNegativeButtonClick: () -> Unit = {},
    animation: JFXAlertAnimation = JFXAlertAnimation.CENTER_ANIMATION,
    critical: Boolean = true
): JFXAlert<Any> {
    val alert = JFXAlert<Any>(stage)
    if (critical) {
        alert.initModality(Modality.APPLICATION_MODAL)
        alert.isOverlayClose = false
    } else {
        alert.initModality(Modality.NONE)
        alert.isOverlayClose = true
    }
    alert.animation = animation
    val layout = JFXDialogLayout()
    layout.setHeading(Label(heading))
    layout.setBody(body)
    if (!positiveButton.isNullOrBlank()) {
        val button = JFXButton(positiveButton)
        button.setOnAction {
            onPositiveButtonClick()
            alert.hideWithAnimation()
        }
        layout.actions.add(button)
    }
    if (!negativeButton.isNullOrBlank()) {
        val button = JFXButton(negativeButton)
        button.setOnAction {
            onNegativeButtonClick()
            alert.hideWithAnimation()
        }
        layout.actions.add(button)
    }
    alert.setContent(layout)
    return alert
}