package p4ulor.obj.detector.ui.screens.home.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

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
    val annotatedStringBuilder = AnnotatedString.Builder()

    var lastIndex = 0
    boldRegex.findAll(text).forEach { result ->
        val before = text.substring(lastIndex, result.range.first)
        val boldText = result.groupValues[1]
        annotatedStringBuilder.append(before)
        annotatedStringBuilder.pushStyle(SpanStyle(fontWeight = FontWeight.Black))
        annotatedStringBuilder.append(boldText)
        annotatedStringBuilder.pop() // stops bold formatting for any following text
        lastIndex = result.range.last + 1
    }

    annotatedStringBuilder.append(text.substring(lastIndex))
    return annotatedStringBuilder.toAnnotatedString()
}

private const val bulletChar = '\u2022'

/**
 * AnnotatedString could be used, but I want to keep it simple.
 * And this will also have much greater performance since it's run once in [Message]
 * In rare occasions Gemini seems like it can also through sub bullet points, that may be use
 * tabs or something. So this solution isn't a catch all
 */
fun String.parseMarkdownBulletPoints() =
    replace("\n* ** ", "\n$bulletChar **")
    .replace("\n* ", "\n$bulletChar ")

@Preview
@Composable
private fun MarkdownParserPreview() = Surface {
    Column {
        Text(
            parseMarkdownBold(
                """
                * ** This a list **
                * ** This a list **
                ** This a list **:
                **This a list**:
                * apple
                * orange
                * extra
                """.trimMargin().trimIndent().parseMarkdownBulletPoints()
            ),
            Modifier.wrapContentSize()
        )
    }
}