package p4ulor.mediapipe.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.flow.first
import org.koin.androidx.compose.koinViewModel
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.viewmodels.SettingsViewModel
import p4ulor.mediapipe.data.domains.gemini.GEMINI_AI_STUDIO_LINK
import p4ulor.mediapipe.data.sources.local.preferences.UserPreferences
import p4ulor.mediapipe.data.sources.local.preferences.UserSecretPreferences
import p4ulor.mediapipe.data.utils.trimToDecimals
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.animations.smooth
import p4ulor.mediapipe.ui.components.CircleThumbCustom
import p4ulor.mediapipe.ui.components.DropdownOptions
import p4ulor.mediapipe.ui.components.IconSmallSize
import p4ulor.mediapipe.ui.components.MaterialIcons
import p4ulor.mediapipe.ui.components.MaterialIconsExt
import p4ulor.mediapipe.ui.components.QuickIcon
import p4ulor.mediapipe.ui.components.QuickText
import p4ulor.mediapipe.ui.components.SliderTrackCustom
import p4ulor.mediapipe.ui.components.geminiLikeText
import p4ulor.mediapipe.ui.components.mediaPipeLikeText
import p4ulor.mediapipe.ui.components.utils.textWidthOf
import p4ulor.mediapipe.ui.components.utils.toast
import p4ulor.mediapipe.ui.theme.PreviewComposable

private val GeneralPadding = 12.dp
private val HorizontalPadding = 8.dp

@Composable
fun SettingsScreen() {
    val vm = koinViewModel<SettingsViewModel>()
    var currentPrefs by remember { mutableStateOf(UserPreferences()) }
    var currentSecretPrefs by remember { mutableStateOf(UserSecretPreferences()) }
    val hasConnection by vm.network.hasConnection.collectAsState(initial = false) // Is cancelled when leaving composition
    var isLoaded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) { // Collect only on first composition rendering
        currentPrefs = vm.getUserPrefs().first()
        currentSecretPrefs = vm.getUserSecretPrefs().first()
        isLoaded = true
    }

    AnimatedVisibility(
        visible = isLoaded,
        enter = fadeIn(smooth()) + scaleIn()
    ) {
        SettingsScreenUi(
            hasConnection,
            currentPrefs,
            currentSecretPrefs,
            saveUserPrefs = {
                vm.saveUserPrefs(it)
            },
            saveUserSecretPrefs = {
                vm.saveUserSecretPrefs(it)
            }
        )
    }
}

@Composable
private fun SettingsScreenUi(
    hasConnection: Boolean,
    userPreferences: UserPreferences,
    userSecretPreferences: UserSecretPreferences,
    saveUserPrefs: (UserPreferences) -> Unit,
    saveUserSecretPrefs: (UserSecretPreferences) -> Unit
) = Surface(Modifier.fillMaxSize(), color = Color.Transparent) {
    Column(Modifier.padding(GeneralPadding), horizontalAlignment = Alignment.CenterHorizontally) {
        MediaPipeSettings(
            currPrefs = userPreferences,
            onNewPrefs = { saveUserPrefs(it) }
        )

        Spacer(Modifier.size(GeneralPadding * 2))

        GeminiSettings(
            currPrefs = userSecretPreferences,
            onNewPrefs = { saveUserSecretPrefs(it) }
        )

        Spacer(Modifier.size(GeneralPadding * 2))

        ConnectivityStatus(hasConnection)

        Spacer(Modifier.size(GeneralPadding * 2))

        SavePicturesCheckBox(
            currPrefs = userPreferences,
            onNewPrefs = { saveUserPrefs(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.MediaPipeSettings(currPrefs: UserPreferences, onNewPrefs: (UserPreferences) -> Unit) {
    val SliderTrackHeight = 10.dp

    var minDetectCertainty by remember { mutableFloatStateOf(currPrefs.minDetectCertainty) }
    var maxObjectsDetections by remember { mutableIntStateOf(currPrefs.maxObjectDetections) }
    var enableAnimations by remember { mutableStateOf(currPrefs.enableAnimations) }

    val detectionCertaintyRange = UserPreferences.Companion.Ranges.detectionCertainty
    val objectDetectionsRange = UserPreferences.Companion.Ranges.objectDetections
    val models = UserPreferences.Companion.Ranges.model

    SettingsHeader(mediaPipeLikeText(R.string.mediapipe))

    Row {
        QuickText(R.string.minimum_detection_certainty)
        Text(
            "${(minDetectCertainty*100).toInt()}%",
            Modifier.width(textWidthOf("%%%%%")), // So the texts don't slightly change positions when slider goes through 0%-100$
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
    BoxWithConstraints {
        Slider(
            value = minDetectCertainty,
            onValueChange = { minDetectCertainty = it.trimToDecimals(2) },
            onValueChangeFinished = {
                onNewPrefs(currPrefs.apply {
                    this.minDetectCertainty = minDetectCertainty
                })
            },
            modifier = Modifier
                .padding(GeneralPadding)
                .widthIn(0.dp, maxWidth * 0.8f),
            valueRange = detectionCertaintyRange.start..detectionCertaintyRange.endInclusive,
            track = { it.SliderTrackCustom(SliderTrackHeight) }
        )
    }

    Row {
        QuickText(R.string.maximum_simultaneous_object_detections)
        Text(maxObjectsDetections.toString(), fontWeight = FontWeight.Bold)
    }

    BoxWithConstraints {
        Slider(
            value = maxObjectsDetections.toFloat(),
            onValueChange = {
                maxObjectsDetections = it.toInt()
            },
            onValueChangeFinished = {
                onNewPrefs(currPrefs.apply {
                    this.maxObjectDetections = maxObjectsDetections
                })
            },
            modifier = Modifier
                .padding(GeneralPadding)
                .widthIn(0.dp, maxWidth * 0.8f),
            valueRange = objectDetectionsRange.first.toFloat()..objectDetectionsRange.last.toFloat(),
            steps = objectDetectionsRange.last - 2, // I don't know why slider puts 2 extra positions
            thumb = { CircleThumbCustom() },
            track = {
                SliderDefaults.Track(
                    sliderState = it,
                    Modifier.size(width = maxWidth * 0.8f, height = SliderTrackHeight),
                    thumbTrackGapSize = 0.dp
                )
            }
        )
    }

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        QuickText(R.string.detection_animations)
        WidthSpacer(GeneralPadding)
        Switch(
            checked = enableAnimations,
            onCheckedChange = {
                enableAnimations = !enableAnimations
                onNewPrefs(currPrefs.apply {
                    this.enableAnimations = it
                })
            },
            colors = SwitchDefaults.colors(
                uncheckedTrackColor = MaterialTheme.colorScheme.scrim
            )
        )
    }

    Spacer(Modifier.size(GeneralPadding))

    DropdownOptions(
        label = R.string.model,
        preSelectedOption = currPrefs.selectedModel,
        options = models,
        horizontalPadding = HorizontalPadding,
        onNewOption = {
            onNewPrefs(currPrefs.apply {
                this.selectedModel = it
            })
        }
    )
}

@Composable
private fun ColumnScope.GeminiSettings(currPrefs: UserSecretPreferences, onNewPrefs: (UserSecretPreferences) -> Unit) {
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
        label = { QuickText(R.string.enter_api_key) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done // Input Method Editor Action
        ),
        singleLine = true,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            Row {
                QuickIcon(
                    MaterialIconsExt.OpenInNew,
                    IconSmallSize
                ) {
                    val openInBrowser = Intent(Intent.ACTION_VIEW, Uri.parse(GEMINI_AI_STUDIO_LINK))
                    ctx.startActivity(openInBrowser)
                }
                QuickIcon(
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
        QuickText(R.string.save)
    }
}

@Composable
private fun ConnectivityStatus(hasConnection: Boolean) {
    val (animation, modifier) = if (hasConnection) { // Because the lottie files have different paddings...
        R.raw.success_animation to Modifier
            .size(30.dp, 30.dp)
            .scale(3f)
            .padding(horizontal = 3.dp)
    } else {
        R.raw.error_animation to Modifier
            .size(30.dp, 30.dp)
            .padding(horizontal = 2.dp)
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animation))
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        QuickText(R.string.connectivity_status)
        LottieAnimation(composition, modifier)
    }
}

@Composable
private fun SavePicturesCheckBox(currPrefs: UserPreferences, onNewPrefs: (UserPreferences) -> Unit) {
    var savePictures by remember { mutableStateOf(currPrefs.savePictures) }

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        QuickText(R.string.save_pictures)
        WidthSpacer(HorizontalPadding)
        Checkbox(
            checked = savePictures,
            onCheckedChange = {
                savePictures = !savePictures
                onNewPrefs(currPrefs.apply {
                    this.savePictures = it
                })
            },
            colors = CheckboxDefaults.colors(
                uncheckedColor = MaterialTheme.colorScheme.scrim
            )
        )
    }
}

@Composable
private fun ColumnScope.SettingsHeader(styledText: AnnotatedString){
    Text(
        styledText,
        Modifier
            .padding(bottom = GeneralPadding)
            .align(Alignment.Start),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.headlineSmall,
    )
}

@Composable
private fun WidthSpacer(width: Dp) = Spacer(Modifier.width(width))

@Preview
@Composable
private fun SettingsScreenPreview() = PreviewComposable {
    SettingsScreenUi(
        hasConnection = false,
        UserPreferences(),
        UserSecretPreferences(),
        {},
        {}
    )
}