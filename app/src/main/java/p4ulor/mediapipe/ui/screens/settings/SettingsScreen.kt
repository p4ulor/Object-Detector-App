package p4ulor.mediapipe.ui.screens.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import p4ulor.mediapipe.R
import p4ulor.mediapipe.data.utils.round
import p4ulor.mediapipe.data.utils.toStringUpTo
import p4ulor.mediapipe.data.utils.trimToDecimals
import p4ulor.mediapipe.ui.components.IconSmallSize
import p4ulor.mediapipe.ui.components.QuickIcon
import p4ulor.mediapipe.ui.components.MaterialIcons
import p4ulor.mediapipe.ui.components.QuickText
import p4ulor.mediapipe.ui.components.geminiLikeText
import p4ulor.mediapipe.ui.components.mediaPipeLikeText
import p4ulor.mediapipe.ui.theme.AppTheme
import p4ulor.mediapipe.ui.theme.ColorSchemeGradient

private val GeneralPadding = 12.dp

@Composable
fun SettingsScreen() = Surface(Modifier.fillMaxSize(), color = Color.Transparent) {
    Column(Modifier.padding(GeneralPadding), horizontalAlignment = Alignment.CenterHorizontally) {
        MediaPipeSettings()
        GeminiSettings()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.MediaPipeSettings() {
    SettingsHeader(mediaPipeLikeText(R.string.mediapipe))

    var detectionSensitivity by remember { mutableFloatStateOf(0.5f) }
    Row {
        QuickText(R.string.detection_sensitivity)
                                    // this seems to be wrong
        Text(detectionSensitivity.toStringUpTo(decimals = 2), fontWeight = FontWeight.Bold)
    }
    BoxWithConstraints {
        Slider(
            value = detectionSensitivity,
            onValueChange = { detectionSensitivity = it.trimToDecimals(2) }, // this seems to be wrong
            Modifier.padding(GeneralPadding).widthIn(0.dp, maxWidth * 0.8f),
            valueRange = 0f..1f,
            track = { sliderPosition ->
                val trackColor = ColorSchemeGradient()
                Canvas(Modifier.fillMaxWidth().height(10.dp)) {
                    drawRoundRect(
                        brush = trackColor,
                        size = Size(size.width * sliderPosition.value, size.height),
                        cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
                    )
                    drawRoundRect(
                        brush = trackColor,
                        size = Size(size.width, size.height),
                        cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
                        blendMode = BlendMode.Overlay
                    )
                }
            }
        )
    }

    var maximumObjectsDetection by remember { mutableStateOf(1f) }
    val maxOfMaximumObjectsDetection = 4

    Row {
        QuickText(R.string.maximum_simultaneous_object_detection)
        Text(maxOfMaximumObjectsDetection.toString(), fontWeight = FontWeight.Bold)
    }

    BoxWithConstraints {
        val color = MaterialTheme.colorScheme.primary
        Slider(
            value = maximumObjectsDetection,
            onValueChange = { maximumObjectsDetection = it },
            Modifier.padding(GeneralPadding).widthIn(0.dp, maxWidth * 0.8f),
            valueRange = 1f..maxOfMaximumObjectsDetection.toFloat(),
            steps = maxOfMaximumObjectsDetection,
            thumb = {
                Canvas(Modifier.size(23.dp))  {
                    drawCircle(color)
                }
            },
            track = {
                SliderDefaults.Track(it, thumbTrackGapSize = 0.dp, trackInsideCornerSize = 0.dp)
            }
        )
    }
}

@Composable
private fun ColumnScope.GeminiSettings(){
    SettingsHeader(geminiLikeText(R.string.gemini_api_key))

    var apiKey by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = apiKey,
        onValueChange = { apiKey = it },
        label = { QuickText(R.string.enter_api_key) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = GeneralPadding),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            QuickIcon(
                if (isVisible) MaterialIcons.VisibilityOff else MaterialIcons.Visibility,
                IconSmallSize
            ) {
                isVisible = !isVisible
            }
        }
    )

    Button(
        onClick = {
            println("Saving API Key: $apiKey") //todo in shared preferences, encrypted
        }
    ) {
        QuickText(R.string.save)
    }
}

@Composable
private fun ColumnScope.SettingsHeader(styledText: AnnotatedString){
    Text(
        styledText,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.padding(bottom = GeneralPadding).align(Alignment.Start)
    )
}

@Preview
@Composable
fun SettingsScreenPreview() = AppTheme(enableDarkTheme = true) {
    Surface {
        SettingsScreen()
    }
}