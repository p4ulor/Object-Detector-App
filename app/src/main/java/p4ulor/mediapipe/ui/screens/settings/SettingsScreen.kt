package p4ulor.mediapipe.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import p4ulor.mediapipe.R
import p4ulor.mediapipe.ui.components.MaterialIcons
import p4ulor.mediapipe.ui.theme.AppTheme
import p4ulor.mediapipe.ui.theme.geminiLikeText

private val GeneralPadding = 12.dp

@Composable
fun SettingsScreen() = Box(Modifier.fillMaxSize()) {
    Column(
        Modifier.padding(GeneralPadding)
    ) {

        Text(
            geminiLikeText(stringResource(R.string.gemini_api_key)),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(bottom = GeneralPadding)
        )

        var apiKey by remember { mutableStateOf("") }
        var isVisible by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text(stringResource(R.string.enter_api_key)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = GeneralPadding),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = { // Add the toggle button
                val image = if (isVisible) MaterialIcons.VisibilityOff else MaterialIcons.Visibility

                val description = if (isVisible) "Hide password" else "Show password"

                IconButton(onClick = { isVisible = !isVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        )

        Button(
            onClick = {
                // Handle saving the API key here (e.g., to SharedPreferences)
                println("Saving API Key: $apiKey")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.save))
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() = AppTheme(enableDarkTheme = true) {
    SettingsScreen()
}