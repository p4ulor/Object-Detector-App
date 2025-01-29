package p4ulor.mediapipe.ui.screens.home

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import p4ulor.mediapipe.ui.animations.smooth
import p4ulor.mediapipe.ui.components.ChatMessage
import p4ulor.mediapipe.ui.theme.AppTheme

private val GeneralPadding = 4.dp

@Composable
fun GeminiChat(newMessage: Message){
    val messages = remember { mutableStateListOf<Message>() }

    LaunchedEffect(newMessage) {
        val isLoading = messages.getOrNull(0)?.isLoading == true
        if (isLoading && !newMessage.authorIsUser) {
            messages[0] = newMessage
        } else if (!isLoading) {
            messages.add(0, newMessage)
        }
    }

    Column(Modifier.fillMaxSize().padding(GeneralPadding)) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages.toList()) { message ->
                ChatMessage(
                    text = message.text,
                    authorisUser = message.authorIsUser,
                    isLoading = message.isLoading,
                    modifier = Modifier.animateItem(
                        fadeInSpec = smooth(),
                        fadeOutSpec = smooth(),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    )
                )
            }
        }
    }
}

/** Run in interactive mode */
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GeminiChatPreview() = AppTheme {
    val newMessageTrigger = remember { MutableStateFlow<Message?>(null) }
    val message by newMessageTrigger.collectAsState()

    LaunchedEffect(Unit) {
        val _dummyMessages = listOf(
            Message("Describe the image"),
            Message("Showcases technology and creativity", authorIsUser = false),
            Message("Think really hard"),
            Message("Loading", authorIsUser = false, isLoading = true),
            Message("I just wasted neurons", authorIsUser = false)
        )
        val dummyMessages = _dummyMessages.toMutableList()
        var maxCycles = _dummyMessages.size * 3
        while (this.isActive && maxCycles-- != 0) {
            if (dummyMessages.isEmpty()) {
                dummyMessages.addAll(_dummyMessages)
            }
            delay(2000)
            newMessageTrigger.emit(dummyMessages.removeAt(0))
        }
    }

    Box(Modifier.fillMaxSize()) {
        message?.let {
            GeminiChat(
                newMessage = it
            )
        }
    }
}

data class Message(
    val text: String,
    val authorIsUser: Boolean = true,
    val isLoading: Boolean = false
)