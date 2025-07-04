package p4ulor.obj.detector.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import p4ulor.obj.detector.R
import p4ulor.obj.detector.data.sources.cloud.gemini.GEMINI_AI_STUDIO_LINK
import p4ulor.obj.detector.data.sources.local.preferences.UserSecretPreferences
import p4ulor.obj.detector.i
import p4ulor.obj.detector.ui.components.IconSmallSize
import p4ulor.obj.detector.ui.components.MaterialIcons
import p4ulor.obj.detector.ui.components.MaterialIconsExt
import p4ulor.obj.detector.ui.components.EzIcon
import p4ulor.obj.detector.ui.components.EzText
import p4ulor.obj.detector.ui.components.geminiLikeText
import p4ulor.obj.detector.ui.components.utils.CenteredColumn
import p4ulor.obj.detector.ui.components.utils.GeneralPadding
import p4ulor.obj.detector.ui.components.utils.toast
import p4ulor.obj.detector.ui.theme.PreviewComposable

@Composable
fun ColumnScope.GeminiSettings(currPrefs: UserSecretPreferences, onNewPrefs: (UserSecretPreferences) -> Unit) {
    val ctx = LocalContext.current
    var apiKey by remember { mutableStateOf(currPrefs.geminiApiKey) }
    var isVisible by remember { mutableStateOf(false) }

    SettingsHeader(geminiLikeText(R.string.gemini_api))

    OutlinedTextField(
        value = apiKey,
        onValueChange = { apiKey = it },
        Modifier
            .fillMaxWidth()
            .padding(bottom = GeneralPadding),
        label = { EzText(R.string.enter_api_key) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done // Input Method Editor Action
        ),
        singleLine = true,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            Row {
                EzIcon(
                    MaterialIconsExt.OpenInNew,
                    IconSmallSize
                ) {
                    val openInBrowser = Intent(Intent.ACTION_VIEW, Uri.parse(GEMINI_AI_STUDIO_LINK))
                    ctx.startActivity(openInBrowser)
                }
                EzIcon(
                    if (isVisible) MaterialIcons.VisibilityOff else MaterialIcons.Visibility,
                    IconSmallSize
                ) {
                    isVisible = !isVisible
                }
            }
        }
    )

    Button(
        onClick = {
            i("Saving API Key")
            onNewPrefs(UserSecretPreferences(apiKey))
            ctx.toast(R.string.saved_gemini_key)
        }
    ) {
        EzText(R.string.save)
    }
}

@Preview
@Composable
private fun GeminiSettingsPreview() = PreviewComposable {
    CenteredColumn {
        GeminiSettings(
            UserSecretPreferences(),
            {}
        )
    }
}
