package p4ulor.mediapipe.ui.components.chat

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import p4ulor.mediapipe.R
import p4ulor.mediapipe.ui.components.utils.roundMessageBox
import p4ulor.mediapipe.ui.theme.PreviewComposable

private val GeneralPadding = 4.dp
private val HorizontalPadding = 8.dp
private val PaddingInsideCard = 16.dp

@Composable
fun ChatMessage(
    text: String = "",
    authorisUser: Boolean = false,
    isPending: Boolean = false, //todo, see if we can remove this, and only use isLoaded
    isLoaded: Boolean = false,
    modifier: Modifier = Modifier,
    isAnimationInProgress: (Boolean) -> Unit = {}
) {
    var animatedText by remember { mutableStateOf("") }

    if (!authorisUser && !isLoaded && !isPending) {
        isAnimationInProgress(true)
        LaunchedEffect(text) {
            animatedText = ""
            text.forEachIndexed { index, _ ->
                delay(5)
                animatedText = text.substring(0, index + 1)
            }
            isAnimationInProgress(false)
        }
    }

    Column(
        modifier
            .padding(horizontal = HorizontalPadding, vertical = GeneralPadding)
            .fillMaxWidth(),
        horizontalAlignment = if (authorisUser) Alignment.End else Alignment.Start,
    ) {

        Text(
            text = if (authorisUser) {
                stringResource(R.string.you)
            } else {
                stringResource(R.string.gemini)
            },
            Modifier.padding(GeneralPadding),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold
        )

        BoxWithConstraints {
            val backgroundColor = if (authorisUser) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
            Card(
                Modifier.widthIn(0.dp, maxWidth * 0.9f),
                shape = roundMessageBox(authorisUser),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
            ) {
                if (isPending) {
                    CircularProgressIndicator(Modifier.padding(PaddingInsideCard))
                } else {
                    Text(
                        if(authorisUser || isLoaded) text else animatedText,
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
            "Witness the convergence of advanced technology and creative vision. " +
                    "This image encapsulates the spirit of modern photography.",
            authorisUser = true,
            isPending = false
        )

        ChatMessage(
            "Witness the convergence of advanced technology and creative vision. " +
                    "This image encapsulates the spirit of modern photographyyyyyyyyyyyyyyyyyyyyyyyyyy.",
            authorisUser = false,
            isPending = false
        )

        ChatMessage(
            "Loading",
            authorisUser = false,
            isPending = true
        )
    }
}