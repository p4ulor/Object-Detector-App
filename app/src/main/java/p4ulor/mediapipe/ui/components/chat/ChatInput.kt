package p4ulor.mediapipe.ui.components.chat

import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.utils.camera.Picture
import p4ulor.mediapipe.ui.components.IconMediumSize
import p4ulor.mediapipe.ui.components.IconSmallSize
import p4ulor.mediapipe.ui.components.MaterialIcons
import p4ulor.mediapipe.ui.components.MaterialIconsExt
import p4ulor.mediapipe.ui.components.QuickIcon
import p4ulor.mediapipe.ui.components.QuickText
import p4ulor.mediapipe.ui.components.utils.RoundRectangleShape
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
    var input by rememberSaveable { mutableStateOf("") }
    val ctx = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

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
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(pictureTaken.path)
                            .crossfade(true)
                            .build(),
                        contentDescription = "picture taken preview",
                        Modifier
                            .size(IconMediumSize)
                            .clip(RoundRectangleShape)
                            .clickable {
                                ctx.startActivity(Intent().apply {
                                    action = Intent.ACTION_VIEW
                                    setDataAndType(pictureTaken.path, pictureTaken.mimeType.value)
                                })
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
}

@Preview
@Composable
private fun ChatInputPreview() = PreviewComposable {
    ChatInput(Modifier.fillMaxWidth(), null, disableSubmit = false, onValidUserSubmit = {

    })
}