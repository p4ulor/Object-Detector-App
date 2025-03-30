package p4ulor.mediapipe.ui.screens.home.chat

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import p4ulor.mediapipe.R
import p4ulor.mediapipe.ui.components.utils.GeneralPaddingSmall
import p4ulor.mediapipe.ui.components.utils.HorizontalPadding
import p4ulor.mediapipe.ui.components.utils.roundMessageBox
import p4ulor.mediapipe.ui.theme.PreviewComposable


private val PaddingInsideCard = 16.dp

/**
 * TODO, maybe this could be improved by having a GeminiMessage and a UserMessage composable
 * and doing the same with the [Message] class
 */
@Composable
fun ChatMessage(
    msg: Message,
    modifier: Modifier = Modifier,
    isAnimationInProgress: (Boolean) -> Unit = {}
) = with(msg) {
    var animatedText by remember { mutableStateOf(AnnotatedString("")) }

    if (isNewGeminiMsg) {
        isAnimationInProgress(true)
        LaunchedEffect(text) {
            var currentText = StringBuilder("")
            text.forEachIndexed { index, char ->
                delay(5)
                currentText.append(char)
                animatedText = parseMarkdownBold(currentText.toString())
            }
            isAnimationInProgress(false)
        }
    }

    Column(
        modifier
            .padding(horizontal = HorizontalPadding, vertical = GeneralPaddingSmall)
            .fillMaxWidth(),
        horizontalAlignment = if (authorIsUser) Alignment.End else Alignment.Start,
    ) {

        Text(
            text = if (authorIsUser) {
                stringResource(R.string.you)
            } else {
                stringResource(R.string.gemini)
            },
            Modifier.padding(GeneralPaddingSmall),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        BoxWithConstraints {
            val backgroundColor = if (authorIsUser) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
            Card(
                Modifier.widthIn(0.dp, maxWidth * 0.9f),
                shape = roundMessageBox(authorIsUser),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
            ) {
                if (isPending) {
                    CircularProgressIndicator(Modifier.padding(PaddingInsideCard))
                } else {
                    Text(
                        if(authorIsUser || isLoaded) parseMarkdownBold(text) else animatedText,
                        Modifier.padding(PaddingInsideCard).animateContentSize()
                    )
                }
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun ChatMessagePreview() = PreviewComposable {
    Column(Modifier.fillMaxSize()) {
        ChatMessage(
            Message(
                "Witness the convergence of advanced technology and creative vision. " +
                        "This image encapsulates the spirit of modern photography.",
                authorIsUser = true,
                isPending = false
            )
        )

        ChatMessage(
            Message(
                "**Witness** the convergence of advanced technology and creative vision. " +
                        "This image encapsulates **the spirit** of modern photographyyyyyyyyyyyyyyyyyyyyyyyyyy.",
                authorIsUser = false,
                isPending = false
            )
        )

        ChatMessage(
            Message(
                authorIsUser = false,
                isPending = true
            )
        )
    }
}