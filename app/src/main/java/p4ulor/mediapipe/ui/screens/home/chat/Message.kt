package p4ulor.mediapipe.ui.screens.home.chat

import p4ulor.mediapipe.data.domains.gemini.GeminiResponse
import java.util.UUID

/** Is used with [GeminiChat]. Messages with isPending, should show a circular loading animation */
data class Message(
    val text: String = "",
    val authorIsUser: Boolean = true,
    var isPending: Boolean = false,
    var isLoaded: Boolean = false,
    val uuid: String = UUID.randomUUID().toString().take(5)
) {
    override fun equals(other: Any?) = uuid == (other as? Message)?.uuid

    val isBlank get() = authorIsUser && text.isBlank()
    val isNewGeminiMsg get() = !authorIsUser && !isLoaded && !isPending

    companion object {
        /** Util to handle null Messages so they are ignored because they are empty */
        val getBlank: Message
            get() = Message()

        val getPending: Message
            get() = Message(authorIsUser = false, isPending = true)

        fun createGeminiMessage(text: String) = Message(text, authorIsUser = false)

        fun from(resp: GeminiResponse?): Message? {
            return resp?.let {
                Message(it.generatedText, authorIsUser = false)
            }
        }
    }
}
