package p4ulor.mediapipe.ui.screens.home.chat

import androidx.compose.material3.Text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Creates an [AnnotatedString] which is like a string with effects and decorations. This type
 * is directly supported by [Text]. This only adds bold to characters surrounded by asterisk.
 */
fun parseMarkdownBold(text: String): AnnotatedString {
    /**
     * 1. \\*\\* -> matches **. The \\ is used to escape the *
     * 2. (.*?) — Captures the text inside **...**
     *      - () creates a capture group, letting us grab the bold text between the asterisks.
     *      - . to match any character
     *      - *? — match zero or more characters, but as few as possible (non-greedy matching)
     */
    val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
    val annotatedString = AnnotatedString.Builder()

    var lastIndex = 0
    boldRegex.findAll(text).forEach { result ->
        val before = text.substring(lastIndex, result.range.first)
        val boldText = result.groupValues[1]
        annotatedString.append(before)
        annotatedString.pushStyle(SpanStyle(fontWeight = FontWeight.Black))
        annotatedString.append(boldText)
        annotatedString.pop() // stops bold formatting for any following text
        lastIndex = result.range.last + 1
    }

    annotatedString.append(text.substring(lastIndex))
    return annotatedString.toAnnotatedString()
}
