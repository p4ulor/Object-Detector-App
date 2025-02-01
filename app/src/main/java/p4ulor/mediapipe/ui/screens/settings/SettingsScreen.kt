package p4ulor.mediapipe.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import p4ulor.mediapipe.R
import p4ulor.mediapipe.data.utils.trimToDecimals
import p4ulor.mediapipe.ui.components.CircleThumb
import p4ulor.mediapipe.ui.components.IconSmallSize
import p4ulor.mediapipe.ui.components.MaterialIcons
import p4ulor.mediapipe.ui.components.QuickIcon
import p4ulor.mediapipe.ui.components.QuickText
import p4ulor.mediapipe.ui.components.SliderTrack
import p4ulor.mediapipe.ui.components.geminiLikeText
import p4ulor.mediapipe.ui.components.mediaPipeLikeText
import p4ulor.mediapipe.ui.theme.AppTheme

private val GeneralPadding = 12.dp

@Composable
fun SettingsScreen() = Surface(Modifier.fillMaxSize(), color = Color.Transparent) {
    Column(Modifier.padding(GeneralPadding), horizontalAlignment = Alignment.CenterHorizontally) {
        MediaPipeSettings()
        Spacer(Modifier.size(GeneralPadding))
        GeminiSettings()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.MediaPipeSettings() {
    val SliderTrackHeight = 10.dp

    SettingsHeader(mediaPipeLikeText(R.string.mediapipe))

    var minDetectCertainty by remember { mutableFloatStateOf(0.5f) }

    Row {
        QuickText(R.string.minimum_detection_certainty)
        val textMeasurer = rememberTextMeasurer()
        val maxTextSize = with(LocalDensity.current) { textMeasurer.measure("%%%%%").size.width.toDp() }
        Text(
            "${(minDetectCertainty*100).toInt()}%",
            Modifier.width(maxTextSize),
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
    BoxWithConstraints {
        Slider(
            value = minDetectCertainty,
            onValueChange = { minDetectCertainty = it.trimToDecimals(2) },
            Modifier.padding(GeneralPadding).widthIn(0.dp, maxWidth * 0.8f),
            valueRange = 0f..1f,
            track = { it.SliderTrack(SliderTrackHeight) }
        )
    }

    var maximumObjectsDetection by remember { mutableFloatStateOf(5f) }
    val maxOfMaximumObjectsDetection = 5

    Row {
        QuickText(R.string.maximum_simultaneous_object_detections)
        Text(maximumObjectsDetection.toInt().toString(), fontWeight = FontWeight.Bold)
    }

    BoxWithConstraints {
        Slider(
            value = maximumObjectsDetection,
            onValueChange = { maximumObjectsDetection = it },
            Modifier.padding(GeneralPadding).widthIn(0.dp, maxWidth * 0.8f),
            valueRange = 1f..maxOfMaximumObjectsDetection.toFloat(),
            steps = maxOfMaximumObjectsDetection - 2, // I don't know why slider puts 2 extra positions
            thumb = { CircleThumb() },
            track = {
                SliderDefaults.Track(
                    it,
                    modifier = Modifier.size(width = maxWidth * 0.8f, height = SliderTrackHeight),
                    thumbTrackGapSize = 0.dp
                )
            }
        )
    }

    var areAnimationsEnabled by remember { mutableStateOf(true) }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
        QuickText(R.string.detection_animations)
        Switch(
            checked = areAnimationsEnabled,
            onCheckedChange = { areAnimationsEnabled = !areAnimationsEnabled },
            colors = SwitchDefaults.colors(
                uncheckedTrackColor = MaterialTheme.colorScheme.scrim
            )
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
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done // Input Method Editor Action
        ),
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