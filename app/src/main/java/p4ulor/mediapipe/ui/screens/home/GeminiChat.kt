package p4ulor.mediapipe.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import p4ulor.mediapipe.ui.animations.smooth
import p4ulor.mediapipe.ui.components.ChatMessage
import p4ulor.mediapipe.ui.theme.AppTheme
import java.util.UUID
import kotlin.random.Random

private val GeneralPadding = 4.dp

/**
 * A Gemini chat box, where [newMessage]s are added to the list of messages. If the latest message
 * sent is a loading message, and the user send a message, it will be ignored. But if that new
 * message if not from the user and is not a loading message, it will replace the previously loading
 * message.
 * @param [isLoadingOrAnimationInProgress] used to enable/disable new user input
 */
@Composable
fun GeminiChat(newMessage: Message, isLoadingOrAnimationInProgress: (Boolean) -> Unit = {}){
    val messages = remember { mutableStateListOf<Message>() }
    val scrollPosition = rememberLazyListState()

    LaunchedEffect(newMessage) {
        if(newMessage.isLoading) isLoadingOrAnimationInProgress(true)
        val currentMsgIsLoading = messages.getOrNull(0)?.isLoading == true
        if (currentMsgIsLoading && !newMessage.isLoading && !newMessage.authorIsUser) {
            messages[0] = newMessage
            scrollPosition.animateScrollToItem(0)
        } else if (!currentMsgIsLoading) {
            messages.add(0, newMessage)
            scrollPosition.animateScrollToItem(0)
        }
    }

    Column(Modifier.fillMaxSize().padding(GeneralPadding)) {
        LazyColumn(
            state = scrollPosition,
            modifier = Modifier.weight(1f), // necessary for reverseLayout to work. Makes so the LazyColumn to expand and align itself with the bottom edge of the parent Column
            reverseLayout = true // reverses order of the items and show them at the bottom
        ) {
            // The key is required for animateItem to work as it's docs say
            items(messages.toList(), key = { it.uuid }) { message ->
                ChatMessage(
                    text = message.text,
                    authorisUser = message.authorIsUser,
                    isLoading = message.isLoading,
                    modifier = if(message == newMessage) Modifier.animateItem(smooth()) else Modifier,
                    isAnimationInProgress = {
                        isLoadingOrAnimationInProgress(it)
                    }
                )
            }
        }
    }
}

/** Run in interactive mode */
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GeminiChatPreview() = AppTheme {
    val newMessages = remember { MutableStateFlow<Message?>(null) }
    val newMessage by newMessages.collectAsState()
    var isLoadingOrAnimationInProgress by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val dummyMessages = getDummyMessages().toMutableList()
        var maxCycles = dummyMessages.size * 3
        while (this.isActive && maxCycles-- != 0) {
            if (dummyMessages.isEmpty()) {
                dummyMessages.addAll(getDummyMessages())
            }
            val messageToAdd = dummyMessages.removeAt(0)
            while (isLoadingOrAnimationInProgress){
                delay(1000)
                if(!messageToAdd.isLoading && !messageToAdd.authorIsUser) {
                    break
                }
            }

            delay(1000)
            newMessages.emit(messageToAdd)
        }
    }

    Box(Modifier.fillMaxSize()) {
        newMessage?.let {
            GeminiChat(
                newMessage = it,
                isLoadingOrAnimationInProgress = { isLoadingOrAnimationInProgress = it }
            )
        }
    }
}

fun getDummyMessages() = listOf(
    Message("Describe the image"),
    Message("Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity. Showcases technology and creativity", authorIsUser = false),
    Message("Think really hard"),
    Message("Loading", authorIsUser = false, isLoading = true),
    Message("I just wasted neurons", authorIsUser = false)
)

data class Message(
    val text: String,
    val authorIsUser: Boolean = true,
    var isLoading: Boolean = false,
    val uuid: String = UUID.randomUUID().toString() + Random(0).nextInt()
) {
    init {
        if(authorIsUser) {
            isLoading = false
        }
    }
    override fun equals(other: Any?) = uuid == (other as? Message)?.uuid
}