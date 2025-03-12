package p4ulor.mediapipe.ui.screens.home.chat

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.animations.smooth
import p4ulor.mediapipe.ui.components.utils.DisplayHeight
import p4ulor.mediapipe.ui.screens.root.BottomNavigationBarHeight
import p4ulor.mediapipe.ui.theme.PreviewComposable

/**
 * A Gemini chat box, where [newMsg]s are added to the list of messages.
 * If a new message is not from the user and is not a loading message and not a pending, it will replace the previously loading
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
    val density = LocalDensity.current
    val middleOfTheScreen = DisplayHeight / 2

    val messages = remember { mutableStateListOf<Message>() }
    val listState = rememberLazyListState()

    var isFirstMessageVisible by remember { mutableStateOf(true) }
    val transparencyGradient = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black
            ),
            startY = with(density) { - BottomNavigationBarHeight.toPx() },
            endY = with(density) { middleOfTheScreen.toPx() - BottomNavigationBarHeight.toPx() }
        )
    }

    LaunchedEffect(newMsg) {
        if(newMsg.isBlank){
            return@LaunchedEffect
        }
        if(newMsg.isPending) isPendingOrAnimationInProgress(true)
        val currentMsgIsPending = messages.getOrNull(0)?.isPending == true
        if (currentMsgIsPending && newMsg.isNewGeminiMsg) {
            messages[0] = newMsg // replace the pending message with the new message
            listState.animateScrollToItem(0) // scroll down on new messages
        } else if (!currentMsgIsPending) {
            messages.add(0, newMsg)
            listState.animateScrollToItem(0) // scroll down on new messages
        }

        isFirstMessageVisible = listState.layoutInfo
            .visibleItemsInfo
            .any { it.index + 1 == messages.size }
    }

    Column(Modifier
        .fillMaxSize()
        .padding(bottom = chatInputHeight)
        .then(
            if(!isFirstMessageVisible && messages.isNotEmpty()){
                Modifier.drawWithContent {
                    drawContent()
                    drawRect( // Adds fade-out effect
                        transparencyGradient,
                        blendMode = BlendMode.DstIn
                    )
                }
            } else {
                Modifier
            }
        )

    ) { // Column used in order to use weight
        LazyColumn(
            Modifier.weight(1f), // weight is necessary for reverseLayout to work. Makes so the LazyColumn to expand and align itself with the bottom edge of the parent Column
            state = listState,
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
private fun GeminiChatPreview() = PreviewComposable(enableDarkTheme = false) {
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
            pictureTaken = null,
            disableSubmit = isPendingOrAnimationInProgress,
            onValidUserSubmit = {

        })
    }
}

/** To generate messages with new UUIDs */
fun getDummyMessages() = listOf(
    Message("Describe the image"),
    Message("Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity", authorIsUser = false),
    Message("Think really hard"),
    Message.getPending,
    Message("I just wasted neurons", authorIsUser = false)
)
