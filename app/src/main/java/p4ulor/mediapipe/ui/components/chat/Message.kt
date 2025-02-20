package p4ulor.mediapipe.ui.components.chat

import java.util.UUID

/** Is used with [GeminiChat]. Messages with isPending, should show a circular loading animation */
data class Message(
    val text: String = "",
    val authorIsUser: Boolean = true,
    var isPending: Boolean = false,
    var isLoaded: Boolean = false,
    val uuid: String = UUID.randomUUID().toString()
) {
    init {
        if(authorIsUser) {
            isPending = false
            isLoaded = true
        }
    }
    override fun equals(other: Any?) = uuid == (other as? Message)?.uuid

    val isBlank: Boolean
        get() = authorIsUser && text.isBlank()
}
