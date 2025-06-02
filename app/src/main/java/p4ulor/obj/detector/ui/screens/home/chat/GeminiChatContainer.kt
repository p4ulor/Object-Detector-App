package p4ulor.obj.detector.ui.screens.home.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import p4ulor.obj.detector.android.utils.camera.Picture
import p4ulor.obj.detector.ui.components.utils.DisplayHeight
import p4ulor.obj.detector.ui.components.utils.GeneralPaddingMedium
import p4ulor.obj.detector.ui.components.utils.ScreenCenter
import p4ulor.obj.detector.ui.components.utils.ScreenHeight
import p4ulor.obj.detector.ui.theme.PreviewComposable

/**
 * Joins the [GeminiChat] and [ChatInput] for ease of use.
 * [isBlank] [Message]s are ignored
 * @param modifier is expected to define the height
 */
@Composable
fun GeminiChatContainer(
    modifier: Modifier,
    pictureTaken: Picture?,
    newGeminiMessage: Message,
    onValidUserSubmit: (String) -> Unit
) {
    val density = LocalDensity.current

    var newMessage by remember { mutableStateOf(Message()) }
    var isPendingOrAnimationInProgress by remember { mutableStateOf(false) }
    var chatInputHeight by remember { mutableStateOf(0.dp) }
    var base64ImagePreview by remember { mutableStateOf<ImageRequest?>(null) }

    LaunchedEffect(newGeminiMessage) {
        newMessage = newGeminiMessage
    }

    Box(modifier) {
        GeminiChat(
            modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
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
            },
            onShowBase64ImagePreview = {
                base64ImagePreview = it
            }
        )
    }

    // Base 64 image unbounded preview (if user toggled off save pictures in settings)
    AnimatedVisibility(
        visible = base64ImagePreview != null,
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
    ) {
        Popup(
            alignment = Alignment.TopCenter,
            offset = IntOffset(x = 0, y = (ScreenCenter.y / 4)),
            onDismissRequest = { base64ImagePreview = null },
            properties = PopupProperties(focusable = true)
        ) {
            AsyncImage(
                model = base64ImagePreview,
                contentDescription = "picture taken large preview",
                Modifier
                    .height(DisplayHeight / 1.8f)
                    .padding(GeneralPaddingMedium)
            )
        }
    }
}

/** Use a real device for this preview, so you can type. Or see [GeminiChatPreview] */
@Preview
@Composable
fun GeminiChatContainerPreview() = PreviewComposable(enableDarkTheme = false) {
    var geminiResponse by remember { mutableStateOf(Message()) }
    var userPrompt by remember { mutableStateOf(Message()) }
    val picture = remember { // Note: this will only show in real device preview, not in Android Studio compose preview panel
        Picture.Base64("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAApgAAAKYB3X3/OAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAANCSURBVEiJtZZPbBtFFMZ/M7ubXdtdb1xSFyeilBapySVU8h8OoFaooFSqiihIVIpQBKci6KEg9Q6H9kovIHoCIVQJJCKE1ENFjnAgcaSGC6rEnxBwA04Tx43t2FnvDAfjkNibxgHxnWb2e/u992bee7tCa00YFsffekFY+nUzFtjW0LrvjRXrCDIAaPLlW0nHL0SsZtVoaF98mLrx3pdhOqLtYPHChahZcYYO7KvPFxvRl5XPp1sN3adWiD1ZAqD6XYK1b/dvE5IWryTt2udLFedwc1+9kLp+vbbpoDh+6TklxBeAi9TL0taeWpdmZzQDry0AcO+jQ12RyohqqoYoo8RDwJrU+qXkjWtfi8Xxt58BdQuwQs9qC/afLwCw8tnQbqYAPsgxE1S6F3EAIXux2oQFKm0ihMsOF71dHYx+f3NND68ghCu1YIoePPQN1pGRABkJ6Bus96CutRZMydTl+TvuiRW1m3n0eDl0vRPcEysqdXn+jsQPsrHMquGeXEaY4Yk4wxWcY5V/9scqOMOVUFthatyTy8QyqwZ+kDURKoMWxNKr2EeqVKcTNOajqKoBgOE28U4tdQl5p5bwCw7BWquaZSzAPlwjlithJtp3pTImSqQRrb2Z8PHGigD4RZuNX6JYj6wj7O4TFLbCO/Mn/m8R+h6rYSUb3ekokRY6f/YukArN979jcW+V/S8g0eT/N3VN3kTqWbQ428m9/8k0P/1aIhF36PccEl6EhOcAUCrXKZXXWS3XKd2vc/TRBG9O5ELC17MmWubD2nKhUKZa26Ba2+D3P+4/MNCFwg59oWVeYhkzgN/JDR8deKBoD7Y+ljEjGZ0sosXVTvbc6RHirr2reNy1OXd6pJsQ+gqjk8VWFYmHrwBzW/n+uMPFiRwHB2I7ih8ciHFxIkd/3Omk5tCDV1t+2nNu5sxxpDFNx+huNhVT3/zMDz8usXC3ddaHBj1GHj/As08fwTS7Kt1HBTmyN29vdwAw+/wbwLVOJ3uAD1wi/dUH7Qei66PfyuRj4Ik9is+hglfbkbfR3cnZm7chlUWLdwmprtCohX4HUtlOcQjLYCu+fzGJH2QRKvP3UNz8bWk1qMxjGTOMThZ3kvgLI5AzFfo379UAAAAASUVORK5CYII=")
    }
    val use4by3 = true
    val modifier = if (use4by3) {
        Modifier
            .height(ScreenHeight / 2)
            .fillMaxWidth()
    } else {
        Modifier.fillMaxSize()
    }

    LaunchedEffect(Unit) {
        while(this.isActive){
            delay(4000)
            if (userPrompt.text == "stop") break
            geminiResponse = Message.getPending
            delay(1000)
            geminiResponse = Message("You typed ${userPrompt.text}", authorIsUser = false)
        }
    }

    Box(Modifier.fillMaxSize()){
        AnimatedVisibility(visible = true, Modifier.fillMaxSize()) {
            GeminiChatContainer(
                modifier,
                newGeminiMessage = geminiResponse,
                pictureTaken = picture,
                onValidUserSubmit = { text ->
                    userPrompt = Message(text)
                }
            )
        }
    }
}