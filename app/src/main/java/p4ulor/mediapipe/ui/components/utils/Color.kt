package p4ulor.mediapipe.ui.components.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

/**
 * Defines colorStops, which is an array of Colors and their offset (ranges) throughout a gradient.
 * Useful for [Brush.linearGradient]
 */
fun rainbowWith(hueShift: Float, saturation: Float = 1f, lightness: Float = 0.5f) = arrayOf(
    0.0f to Color.hsv((0f + hueShift) % 360f, saturation, lightness),
    0.2f to Color.hsl((60f + hueShift) % 360f, saturation, lightness),
    0.4f to Color.hsl((120f + hueShift) % 360f, saturation, lightness),
    0.6f to Color.hsl((180f + hueShift) % 360f, saturation, lightness),
    0.8f to Color.hsl((240f + hueShift) % 360f, saturation, lightness),
    1f to Color.hsl((300f + hueShift) % 360f, saturation, lightness)
)

// Might be useful
val ColorInvertFilter = ColorFilter.colorMatrix(
    ColorMatrix(
        floatArrayOf(
            -1f, 0f, 0f, 0f, 255f, // Red inversion
            0f, -1f, 0f, 0f, 255f, // Green inversion
            0f, 0f, -1f, 0f, 255f, // Blue inversion
            0f, 0f, 0f, 1f, 0f // Alpha unchanged
        )
    )
)
