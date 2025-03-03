package p4ulor.mediapipe.ui.components.chat

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.utils.camera.Picture
import p4ulor.mediapipe.ui.components.IconMediumSize
import p4ulor.mediapipe.ui.components.IconSmallSize
import p4ulor.mediapipe.ui.components.MaterialIcons
import p4ulor.mediapipe.ui.components.MaterialIconsExt
import p4ulor.mediapipe.ui.components.QuickIcon
import p4ulor.mediapipe.ui.components.QuickText
import p4ulor.mediapipe.ui.components.utils.RoundRectangleShape
import p4ulor.mediapipe.ui.components.utils.base64ToImageRequest
import p4ulor.mediapipe.ui.components.utils.pathToImageRequest
import p4ulor.mediapipe.ui.components.utils.toast
import p4ulor.mediapipe.ui.theme.PreviewComposable

/**
 * @param onValidUserSubmit callback where the [String] input is what the user wrote and submitted
 * @param disableSubmit should only apply regarding the pending or loading status of Gemini ChatMessages
 */
@Composable
fun ChatInput(
    modifier: Modifier,
    pictureTaken: Picture?,
    disableSubmit: Boolean,
    onValidUserSubmit: (String) -> Unit
) {
    val ctx = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var input by rememberSaveable { mutableStateOf("") }
    var base64ImagePreview by remember { mutableStateOf<ImageRequest?>(null) }
    var image by remember { mutableStateOf<ImageRequest?>(null) }
    var isImageFile by remember { mutableStateOf(false) }

    val validateInputAndSubmit = {
        if (input.isBlank()) {
            ctx.toast(R.string.cant_prompt_empty_message)
        } else if(pictureTaken == null){
            ctx.toast(R.string.take_a_picture_first)
        } else if(!disableSubmit && input.isNotBlank()){
            keyboardController?.hide()
            onValidUserSubmit(input)
            input = ""
        }
    }

    LaunchedEffect(pictureTaken) {
        pictureTaken?.let { pic ->
            with(ctx){
                pic.asFile?.path?.let {
                    pathToImageRequest(it) to true
                } ?: pic.asBase64?.base64?.let {
                    base64ToImageRequest(it) to false
                }
            }
        }.let {
            image = it?.first ?: null
            isImageFile = it?.second ?: false
        }
    }

    OutlinedTextField(
        value = input,
        onValueChange = { input = it },
        modifier.border(2.dp, MaterialTheme.colorScheme.outline, RoundRectangleShape),
        placeholder = { QuickText(R.string.ask_gemini) },
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if(pictureTaken == null){
                    QuickIcon(MaterialIcons.NoPhotography, IconSmallSize) {}
                } else {
                    AsyncImage(
                        model = image,
                        contentDescription = "picture taken preview",
                        Modifier
                            .size(IconMediumSize)
                            .clip(RoundRectangleShape)
                            .clickable {
                                if (isImageFile) {
                                    ctx.startActivity(Intent().apply {
                                        action = Intent.ACTION_VIEW
                                        setDataAndType(
                                            pictureTaken.asFile?.path,
                                            pictureTaken.mimeType.value
                                        )
                                    })
                                } else {
                                    base64ImagePreview = image
                                }
                            },
                        contentScale = ContentScale.Crop
                    )
                }
                val sendIcon = if(disableSubmit) MaterialIcons.Block else MaterialIconsExt.Send
                QuickIcon(sendIcon, IconSmallSize) {
                    validateInputAndSubmit()
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Send // Input Method Editor Action
        ),
        keyboardActions = KeyboardActions(
            onSend = {
                validateInputAndSubmit()
            }
        ),
        singleLine = true,
        shape = RoundRectangleShape
    )

    AnimatedVisibility(
        visible = base64ImagePreview != null,
        Modifier.fillMaxSize()
    ) {
        Box(Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)))
        {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { base64ImagePreview = null },
                properties = PopupProperties(focusable = true)
            ) {
                AsyncImage(
                    model = base64ImagePreview,
                    contentDescription = "picture taken large preview",
                    Modifier.height(400.dp)
                )
            }
        }

    }
}

@Preview
@Composable
private fun ChatInputPreview() = PreviewComposable {
    // picture doesn't show on Preview, but shows in real device
    val picture = Picture.Base64("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAApgAAAKYB3X3/OAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAANCSURBVEiJtZZPbBtFFMZ/M7ubXdtdb1xSFyeilBapySVU8h8OoFaooFSqiihIVIpQBKci6KEg9Q6H9kovIHoCIVQJJCKE1ENFjnAgcaSGC6rEnxBwA04Tx43t2FnvDAfjkNibxgHxnWb2e/u992bee7tCa00YFsffekFY+nUzFtjW0LrvjRXrCDIAaPLlW0nHL0SsZtVoaF98mLrx3pdhOqLtYPHChahZcYYO7KvPFxvRl5XPp1sN3adWiD1ZAqD6XYK1b/dvE5IWryTt2udLFedwc1+9kLp+vbbpoDh+6TklxBeAi9TL0taeWpdmZzQDry0AcO+jQ12RyohqqoYoo8RDwJrU+qXkjWtfi8Xxt58BdQuwQs9qC/afLwCw8tnQbqYAPsgxE1S6F3EAIXux2oQFKm0ihMsOF71dHYx+f3NND68ghCu1YIoePPQN1pGRABkJ6Bus96CutRZMydTl+TvuiRW1m3n0eDl0vRPcEysqdXn+jsQPsrHMquGeXEaY4Yk4wxWcY5V/9scqOMOVUFthatyTy8QyqwZ+kDURKoMWxNKr2EeqVKcTNOajqKoBgOE28U4tdQl5p5bwCw7BWquaZSzAPlwjlithJtp3pTImSqQRrb2Z8PHGigD4RZuNX6JYj6wj7O4TFLbCO/Mn/m8R+h6rYSUb3ekokRY6f/YukArN979jcW+V/S8g0eT/N3VN3kTqWbQ428m9/8k0P/1aIhF36PccEl6EhOcAUCrXKZXXWS3XKd2vc/TRBG9O5ELC17MmWubD2nKhUKZa26Ba2+D3P+4/MNCFwg59oWVeYhkzgN/JDR8deKBoD7Y+ljEjGZ0sosXVTvbc6RHirr2reNy1OXd6pJsQ+gqjk8VWFYmHrwBzW/n+uMPFiRwHB2I7ih8ciHFxIkd/3Omk5tCDV1t+2nNu5sxxpDFNx+huNhVT3/zMDz8usXC3ddaHBj1GHj/As08fwTS7Kt1HBTmyN29vdwAw+/wbwLVOJ3uAD1wi/dUH7Qei66PfyuRj4Ik9is+hglfbkbfR3cnZm7chlUWLdwmprtCohX4HUtlOcQjLYCu+fzGJH2QRKvP3UNz8bWk1qMxjGTOMThZ3kvgLI5AzFfo379UAAAAASUVORK5CYII=")
    ChatInput(Modifier.fillMaxWidth(), null, disableSubmit = false, onValidUserSubmit = {

    })
}