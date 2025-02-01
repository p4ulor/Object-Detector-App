package p4ulor.mediapipe.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import p4ulor.mediapipe.data.utils.trimToDecimals
import p4ulor.mediapipe.ui.theme.AppTheme
import p4ulor.mediapipe.ui.theme.ColorSchemeGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderState.SliderTrack(SliderHeight: Dp) {
    val trackColor = ColorSchemeGradient()
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(SliderHeight)) {
        drawRoundRect(
            brush = trackColor,
            size = Size(size.width * this@SliderTrack.value, size.height),
            cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
            colorFilter = ColorFilter.lighting(primaryContainer, Color(0xFF9B9B9B))
        )
        drawRoundRect(
            brush = trackColor,
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
            blendMode = BlendMode.Overlay
        )
    }
}

@Composable
fun CircleThumb(){
    val color = MaterialTheme.colorScheme.primary
    Canvas(Modifier.size(20.dp))  {
        drawCircle(color)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun SliderPreviews() = AppTheme {
    Column {
        val SliderTrackHeight = 10.dp
        var detectionSensitivity by remember { mutableFloatStateOf(0.5f) }

        Slider(
            value = detectionSensitivity,
            onValueChange = { detectionSensitivity = it.trimToDecimals(2) },
            valueRange = 0f..1f,
            track = { it.SliderTrack(SliderTrackHeight) }
        )
    }
}