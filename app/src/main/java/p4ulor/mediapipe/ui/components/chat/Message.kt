package p4ulor.mediapipe.ui.components.chat

import p4ulor.mediapipe.data.domains.gemini.GeminiResponse
import java.util.UUID

/** Is used with [GeminiChat]. Messages with isPending, should show a circular loading animation */
data class Message(
    val text: String = "",
    val authorIsUser: Boolean = true,
    var isPending: Boolean = false,
    var isLoaded: Boolean = false,
    val uuid: String = UUID.randomUUID().toString().take(6)
) {
    override fun equals(other: Any?) = uuid == (other as? Message)?.uuid

    val isBlank: Boolean
        get() = authorIsUser && text.isBlank()

    val isNewGeminiMsg: Boolean
        get() = !authorIsUser && !isLoaded && !isPending

    companion object {
        val getBlank: Message
            get() = Message()

        fun from(resp: GeminiResponse?): Message? {
            return resp?.let {
                Message(it.generatedText, authorIsUser = false)
            }
        }
    }
}
