package p4ulor.mediapipe.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

fun rainbowWith(hueShift: Float, saturation: Float = 1f, lightness: Float = 0.5f) = arrayOf(
    0.0f to Color.hsv((0f + hueShift) % 360f, saturation, lightness),
    0.2f to Color.hsl((60f + hueShift) % 360f, saturation, lightness),
    0.4f to Color.hsl((120f + hueShift) % 360f, saturation, lightness),
    0.6f to Color.hsl((180f + hueShift) % 360f, saturation, lightness),
    0.8f to Color.hsl((240f + hueShift) % 360f, saturation, lightness),
    1f to Color.hsl((300f + hueShift) % 360f, saturation, lightness)
)

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