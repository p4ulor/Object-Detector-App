package p4ulor.mediapipe.ui.components.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import p4ulor.mediapipe.ui.screens.root.BottomNavigationBarHeight

/**
 * Util function to create a [Brush] that can be passed to [fadingEdge] to create a fade in/out
 * of a composable
 */
@Composable
fun TransparencyGradient(position: TransparentGradientPosition) = run {
    Brush.verticalGradient(
        colorStops = position.colorStops,
        startY = 0f,
        endY = with(LocalDensity.current) { DisplayHeight.toPx() - SystemNavigationBarHeight.toPx() - BottomNavigationBarHeight.toPx() }
    )
}

enum class TransparentGradientPosition(vararg val colorStops: Pair<Float, Color>){
    Top(
        0f to Color.Transparent,
        0.2f to Color.Black,
        1f to Color.Black
    ),
    Bottom(
        0f to Color.Black,
        0.8f to Color.Black,
        1f to Color.Transparent
    ),
    Vertical(
        0f to Color.Transparent,
        0.2f to Color.Black,
        0.8f to Color.Black,
        1f to Color.Transparent
    ),
    None(
        0f to Color.Black,
        1f to Color.Black
    )
}

val GeminiLikeGradient = listOf(Color(0xFF2E78F4), Color(0xFFF76744)) // Blue to Red (Similar to Gemini colors)
val MediaPipeLikeGradient = listOf(Color(0xFF5FFFB2), Color(0xFF00FF91))

@Composable
fun ColorSchemeGradient() = Brush.horizontalGradient(listOf(
    MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.primaryContainer
))
