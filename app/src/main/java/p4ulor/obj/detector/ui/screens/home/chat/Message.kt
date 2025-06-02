package p4ulor.obj.detector.ui.screens.home.chat

import p4ulor.obj.detector.data.domains.gemini.GeminiResponse
import java.util.UUID

/** Is used with [GeminiChat] and [ChatMessage].
 * Messages with isPending, should show a circular loading animation
 * @param isPending is true if the app is waiting for the HTTP response
 * @param isLoaded is true once the entire generated text has been loaded (with some typing animation)
 */
data class Message private constructor(
    val text: String,
    val authorIsUser: Boolean,
    var isPending: Boolean,
    var isLoaded: Boolean,
    val uuid: String
) {
    constructor(
        text: String = "",
        authorIsUser: Boolean = true,
        isPending: Boolean = false,
        isLoaded: Boolean = false,
    ) : this (
        text.parseMarkdownBulletPoints(),
        authorIsUser,
        isPending,
        isLoaded,
        UUID.randomUUID().toString().take(5)
    )

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
