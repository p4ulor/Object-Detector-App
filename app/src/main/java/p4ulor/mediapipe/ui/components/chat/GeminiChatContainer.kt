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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.utils.camera.Picture
import p4ulor.mediapipe.ui.components.utils.toast
import p4ulor.mediapipe.ui.theme.PreviewComposable

/**
 * Joins the [GeminiChat] and [ChatInput] for ease of use.
 * [isBlank] [Message]s are ignored
 * Also checkout [GeminiChatPreview].
 */
@Composable
fun GeminiChatContainer(
    modifier: Modifier = Modifier,
    pictureTaken: Picture?,
    newGeminiMessage: Message,
    onValidUserSubmit: (String) -> Unit
) {
    val density = LocalDensity.current
    val ctx = LocalContext.current

    var newMessage by remember { mutableStateOf(Message()) }
    var isPendingOrAnimationInProgress by remember { mutableStateOf(false) }
    var chatInputHeight by remember { mutableStateOf(0.dp) }

    LaunchedEffect(newGeminiMessage) {
        newMessage = newGeminiMessage
    }

    Box(modifier) {
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
            onSubmit = { text ->
                if (text.isBlank()) {
                    ctx.toast(R.string.cant_prompt_empty_message)
                }
                else if(pictureTaken == null){
                    ctx.toast(R.string.take_a_picture_first)
                } else {
                    isPendingOrAnimationInProgress = true
                    newMessage = Message(text)
                    onValidUserSubmit(text)
                }
            }
        )
    }
}

/** Use a real device for this preview, so you can type */
@Preview
@Composable
fun GeminiChatContainerPreview() = PreviewComposable {
    var geminiResponse by remember { mutableStateOf(Message()) }
    var userPrompt by remember { mutableStateOf(Message()) }

    LaunchedEffect(Unit) {
        while (this.isActive) {
            delay(10000)
            geminiResponse = Message(authorIsUser = false, isPending = true)
            delay(5000)
            geminiResponse = Message("You typed ${userPrompt.text}", authorIsUser = false)
            delay(10000)
            geminiResponse = Message(authorIsUser = false, isPending = true)
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