package p4ulor.mediapipe.ui.components.chat

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import p4ulor.mediapipe.ui.animations.smooth
import p4ulor.mediapipe.ui.theme.AppTheme

/**
 * A Gemini chat box, where [newMsg]s are added to the list of messages. If the latest message
 * sent is a loading message, and the user sends a message, it will be ignored. But if that new
 * message is not from the user and is not a loading message, it will replace the previously loading
 * message.
 * @param [isPendingOrAnimationInProgress], an optional callback that can be used to know when
 * a message is pending or animating
 */
@Composable
fun GeminiChat(
    newMsg: Message,
    chatInputHeight: Dp,
    isPendingOrAnimationInProgress: (Boolean) -> Unit = {}
){
    val messages = remember { mutableStateListOf<Message>() }
    val scrollPosition = rememberLazyListState()

    LaunchedEffect(newMsg) {
        if(newMsg.isBlank){
            return@LaunchedEffect
        }
        if(newMsg.isPending) isPendingOrAnimationInProgress(true)
        val currentMsgIsPending = messages.getOrNull(0)?.isPending == true
        if (currentMsgIsPending && !newMsg.isPending && !newMsg.authorIsUser) {
            messages[0] = newMsg // replace the pending message with a loading message
            scrollPosition.animateScrollToItem(0)
        } else if (!currentMsgIsPending) {
            messages.add(0, newMsg)
            scrollPosition.animateScrollToItem(0)
        }
    }

    Column(Modifier.fillMaxSize().padding(bottom = chatInputHeight)) { // Column used in order to use weight
        LazyColumn(
            state = scrollPosition,
            modifier = Modifier.weight(1f), // weight is necessary for reverseLayout to work. Makes so the LazyColumn to expand and align itself with the bottom edge of the parent Column
            reverseLayout = true // reverses order of the items and show them at the bottom
        ) {
            // The key is required for animateItem to work as it's docs say
            items(messages.toList(), key = { it.uuid }) { message ->
                ChatMessage(
                    message,
                    modifier = if(message == newMsg) Modifier.animateItem(smooth()) else Modifier,
                    isAnimationInProgress = { isIt ->
                        isPendingOrAnimationInProgress(isIt)
                        if(!isIt){
                            message.isLoaded = true
                        }
                    }
                )
            }
        }
    }
}

/**
 * Run in interactive mode.
 * This has some logic to constrain new newMessages but it's all so simulate a real use. New messages
 * are blocked by ChatInput by disabling the click of the trailing icon.
 * Note: Compose preview with interaction mode doesn't allow typing in the [ChatInput]
 */
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GeminiChatPreview() = AppTheme {
    var newMessage by remember { mutableStateOf(Message()) }
    var isPendingOrAnimationInProgress by remember { mutableStateOf(false) }
    var chatInputHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        val dummyMessages = getDummyMessages().toMutableList()
        var maxCycles = dummyMessages.size * 3
        while (this.isActive && maxCycles-- != 0) {
            if (dummyMessages.isEmpty()) {
                dummyMessages.addAll(getDummyMessages())
            }

            val messageToAdd = dummyMessages.removeAt(0)
            val willReplacePreviouslyPendingMsg = !messageToAdd.isPending && newMessage.isPending
            while (isPendingOrAnimationInProgress && !willReplacePreviouslyPendingMsg){
                delay(100)
            }
            if(messageToAdd.isPending){
                isPendingOrAnimationInProgress = true
            }
            newMessage = messageToAdd
            delay(1000)
        }
    }

    Box {
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
            disableSubmit = isPendingOrAnimationInProgress,
            onSubmit = {

        })
    }
}

/** To generate messages with new UUIDs */
fun getDummyMessages() = listOf(
    Message("Describe the image"),
    Message("Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity", authorIsUser = false),
    Message("Think really hard"),
    Message(authorIsUser = false, isPending = true),
    Message("I just wasted neurons", authorIsUser = false)
)
