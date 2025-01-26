package p4ulor.mediapipe.ui.components

import android.content.res.Configuration
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import p4ulor.mediapipe.R
import p4ulor.mediapipe.ui.theme.AppTheme

private val GeneralPadding = 4.dp
private val HorizontalPadding = 8.dp
private val PaddingInsideCard = 16.dp

@Composable
fun ChatMessage(
    message: String = "",
    authorisUser: Boolean = false,
    isLoading: Boolean = false
) {
    val backgroundColor = if (authorisUser) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val horizontalAlignment = if (authorisUser) Alignment.End else Alignment.Start

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = Modifier
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
            Card(
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                shape = roundMessageBox(authorisUser),
                modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.padding(PaddingInsideCard))
                } else {
                    Text(
                        text = message,
                        Modifier.padding(PaddingInsideCard)
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