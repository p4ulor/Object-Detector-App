package p4ulor.obj.detector.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import p4ulor.obj.detector.R
import p4ulor.obj.detector.data.domains.mediapipe.Model
import p4ulor.obj.detector.data.sources.local.preferences.UserPreferences
import p4ulor.obj.detector.data.utils.toPercentage
import p4ulor.obj.detector.data.utils.trimToDecimals
import p4ulor.obj.detector.ui.components.CircleThumbCustom
import p4ulor.obj.detector.ui.components.DropdownOptions
import p4ulor.obj.detector.ui.components.IconSmallSize
import p4ulor.obj.detector.ui.components.MaterialIcons
import p4ulor.obj.detector.ui.components.EzIcon
import p4ulor.obj.detector.ui.components.EzText
import p4ulor.obj.detector.ui.components.SliderTrackCustom
import p4ulor.obj.detector.ui.components.mediaPipeLikeText
import p4ulor.obj.detector.ui.components.utils.GeneralPadding
import p4ulor.obj.detector.ui.components.utils.textWidthOf

private val SliderTrackHeight = 10.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.MediaPipeSettings(currPrefs: UserPreferences, onNewPrefs: (UserPreferences) -> Unit) {
    val ctx = LocalContext.current

    var minDetectCertainty by remember { mutableFloatStateOf(currPrefs.minDetectCertainty) }
    var maxObjectsDetections by remember { mutableIntStateOf(currPrefs.maxObjectDetections) }
    var enableAnimations by remember { mutableStateOf(currPrefs.enableAnimations) }

    val detectionCertaintyRange = UserPreferences.Companion.Ranges.detectionCertainty
    val objectDetectionsRange = UserPreferences.Companion.Ranges.objectDetections
    val models = UserPreferences.Companion.Ranges.modelNames
    val modelsDescriptions = remember { UserPreferences.Companion.Ranges.getModelsDescriptions(ctx) }

    SettingsHeader(mediaPipeLikeText(R.string.mediapipe))

    Row {
        EzText(R.string.minimum_detection_certainty)
        Text(
            text = minDetectCertainty.toPercentage(),
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
        EzText(R.string.maximum_simultaneous_object_detections)
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
        val toolTipState = rememberTooltipState(isPersistent = true)
        val scope = rememberCoroutineScope()

        EzText(R.string.detection_animations)

        TooltipBox(
            positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
            tooltip = {
                RichTooltip(
                    Modifier.padding(horizontal = GeneralPadding),
                    title = { EzText(R.string.note) },
                    action = {},
                    caretSize = TooltipDefaults.caretSize * 2f
                ) {
                    EzText(R.string.detection_animations_details)
                }
            },
            state = toolTipState
        ) {
            EzIcon(MaterialIcons.Info, IconSmallSize) {
                scope.launch { toolTipState.show() }
            }
        }

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

        WidthSpacer(GeneralPadding)
    }

    Spacer(Modifier.size(GeneralPadding))

    DropdownOptions(
        label = R.string.model,
        preSelectedOption = Model.indexOf(currPrefs.selectedModel),
        options = models,
        optionLabels = modelsDescriptions,
        onNewOption = { optionIndex ->
            onNewPrefs(currPrefs.apply {
                this.selectedModel = models[optionIndex]
            })
        }
    )
}
