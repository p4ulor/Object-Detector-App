package p4ulor.mediapipe.ui.components.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import p4ulor.mediapipe.android.utils.camera.Picture
import p4ulor.mediapipe.ui.components.utils.DisplayHeight
import p4ulor.mediapipe.ui.screens.root.BottomNavigationBarHeight
import p4ulor.mediapipe.ui.theme.PreviewComposable

/**
 * Joins the [GeminiChat] and [ChatInput] for ease of use.
 * [isBlank] [Message]s are ignored
 */
@Composable
fun GeminiChatContainer(
    modifier: Modifier = Modifier,
    pictureTaken: Picture?,
    newGeminiMessage: Message,
    onValidUserSubmit: (String) -> Unit
) {
    var newMessage by remember { mutableStateOf(Message()) }
    var isPendingOrAnimationInProgress by remember { mutableStateOf(false) }
    var chatInputHeight by remember { mutableStateOf(0.dp) }

    val density = LocalDensity.current
    val middleOfTheScreen = DisplayHeight / 2
    val transparencyGradient = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Black
        ),
        startY = 0f,
        endY = with(density) { middleOfTheScreen.toPx() - BottomNavigationBarHeight.toPx() }
    )

    LaunchedEffect(newGeminiMessage) {
        newMessage = newGeminiMessage
    }

    Box(modifier
        .drawWithContent {
            drawContent()
            drawRect( // Adds fade-out effect
                transparencyGradient,
                blendMode = BlendMode.DstIn
            )
        }
    ) {
        GeminiChat(
            newMsg = newMessage,
            chatInputHeight = chatInputHeight,
            isPendingOrAnimationInProgress = { isPendingOrAnimationInProgress = it }
        )
        ChatInput(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .onSizeChanged { size ->
                    chatInputHeight = density.run {
                        size.height.toDp()
                    }
                },
            pictureTaken = pictureTaken,
            disableSubmit = isPendingOrAnimationInProgress,
            onValidUserSubmit = { text ->
                isPendingOrAnimationInProgress = true
                newMessage = Message(text)
                onValidUserSubmit(text)
            }
        )
    }
}

/** Use a real device for this preview, so you can type. Or see [GeminiChatPreview] */
@Preview
@Composable
fun GeminiChatContainerPreview() = PreviewComposable(enableDarkTheme = false) {
    var geminiResponse by remember { mutableStateOf(Message()) }
    var userPrompt by remember { mutableStateOf(Message()) }

    LaunchedEffect(Unit) {
        while (this.isActive) {
            delay(10000)
            geminiResponse = Message.getPending
            delay(5000)
            geminiResponse = Message("You typed ${userPrompt.text}", authorIsUser = false)
            delay(10000)
            geminiResponse = Message.getPending
            delay(5000)
            geminiResponse = Message("You typed2 ${userPrompt.text}", authorIsUser = false)
        }
    }

    GeminiChatContainer(
        newGeminiMessage = geminiResponse,
        pictureTaken = null,
        onValidUserSubmit = { text ->
            userPrompt = Message(text)
        }
    )
}