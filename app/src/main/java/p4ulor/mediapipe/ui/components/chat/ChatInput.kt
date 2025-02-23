package p4ulor.mediapipe.ui.components.chat

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.PhotoCameraBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.utils.Picture
import p4ulor.mediapipe.ui.components.IconSmallSize
import p4ulor.mediapipe.ui.components.MaterialIcons
import p4ulor.mediapipe.ui.components.QuickIcon
import p4ulor.mediapipe.ui.components.QuickText
import p4ulor.mediapipe.ui.components.utils.RoundRectangleShape
import p4ulor.mediapipe.ui.theme.PreviewComposable

/**
 * @param onSubmit callback where the [String] input is what the user wrote and submitted
 * @param disableSubmit should only apply regarding the pending or loading status of Gemini ChatMessages
 */
@Composable
fun ChatInput(
    modifier: Modifier,
    pictureTaken: Picture?,
    disableSubmit: Boolean,
    onSubmit: (String) -> Unit
) {
    var input by rememberSaveable { mutableStateOf("") }
    val ctx = LocalContext.current
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = input,
        onValueChange = { input = it },
        modifier
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundRectangleShape),
        placeholder = { QuickText(R.string.ask_gemini) },
        trailingIcon = {
            val icon = if(disableSubmit) MaterialIcons.Block else MaterialIcons.Send
            Row {
                pictureTaken?.run {
                    QuickIcon(MaterialIcons.PhotoCameraBack, IconSmallSize) {
                        ctx.startActivity(Intent().apply {
                            action = Intent.ACTION_VIEW
                            setDataAndType(path, mimeType.value)
                        })
                    }
                }
                QuickIcon(icon, IconSmallSize) {
                    if(!disableSubmit){
                        onSubmit(input)
                        input = ""
                    }
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Send // Input Method Editor Action
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                // Don't submit, only the icon click is used
                focusManager.clearFocus() // todo see if this is needed
            }
        ),
        singleLine = true,
        shape = RoundRectangleShape
    )
}

@Preview
@Composable
private fun ChatInputPreview() = PreviewComposable {
    ChatInput(Modifier.fillMaxWidth(), Picture(Uri.EMPTY), disableSubmit = false, onSubmit = {

    })
}