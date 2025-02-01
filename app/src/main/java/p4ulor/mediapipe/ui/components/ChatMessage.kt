package p4ulor.mediapipe.ui.components

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
import p4ulor.mediapipe.ui.theme.AppTheme

private val GeneralPadding = 4.dp
private val HorizontalPadding = 8.dp
private val PaddingInsideCard = 16.dp

@Composable
fun ChatMessage(
    text: String = "",
    authorisUser: Boolean = false,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    isAnimationInProgress: (Boolean) -> Unit = {}
) {
    var animatedText by remember { mutableStateOf("") }

    if (!authorisUser && !isLoading) {
        isAnimationInProgress(true)
        LaunchedEffect(text) {
            animatedText = ""
            text.forEachIndexed { index, _ ->
                delay(10)
                animatedText = text.substring(0, index + 1)
            }
            isAnimationInProgress(false)
        }
    }

    Column(
        horizontalAlignment = if (authorisUser) Alignment.End else Alignment.Start,
        modifier = modifier
            .padding(horizontal = HorizontalPadding, vertical = GeneralPadding)
            .fillMaxWidth()
    ) {

        Text(
            text = if (authorisUser) {
                stringResource(R.string.you)
            } else {
                stringResource(R.string.gemini)
            },
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(GeneralPadding)
        )

        BoxWithConstraints {
            val backgroundColor = if (authorisUser) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                shape = roundMessageBox(authorisUser),
                modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.padding(PaddingInsideCard))
                } else {
                    Text(
                        if(authorisUser) text else animatedText,
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
fun ChatMessagePreview() = AppTheme {
    Column(Modifier.fillMaxSize()) {
        ChatMessage(
            "Witness the convergence of advanced technology and creative vision. " +
                    "This image encapsulates the spirit of modern photography.",
            authorisUser = true,
            isLoading = false
        )

        ChatMessage(
            "Witness the convergence of advanced technology and creative vision. " +
                    "This image encapsulates the spirit of modern photographyyyyyyyyyyyyyyyyyyyyyyyyyy.",
            authorisUser = false,
            isLoading = false
        )

        ChatMessage(
            "Loading",
            authorisUser = false,
            isLoading = true
        )
    }
}