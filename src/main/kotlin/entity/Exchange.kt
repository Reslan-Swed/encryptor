package entity

import java.io.Serializable

/**
 * created by Reslan
 * lεροrεm-cοδε
 * 2019-12-06
 */
data class Exchange(val senderId: Int, val receiverName: String, val amount: Double, val reason: String) : Serializable {
    companion object {
        private const val serialVersionUID = 521561242549860L
    }
}