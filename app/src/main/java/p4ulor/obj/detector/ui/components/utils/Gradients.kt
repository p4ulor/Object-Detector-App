package p4ulor.obj.detector.ui.components.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import p4ulor.obj.detector.data.domains.mediapipe.Achievement
import p4ulor.obj.detector.data.utils.capitalized
import p4ulor.obj.detector.data.utils.getTodaysDate
import p4ulor.obj.detector.data.utils.toPercentage
import p4ulor.obj.detector.ui.screens.root.BottomNavigationBarHeight
import p4ulor.obj.detector.ui.theme.PreviewComposable

/**
 * Util function to create a [Brush] that can be passed to [fadingEdge] to create a fade in/out
 * of a composable
 */
@Composable
fun TransparencyGradient(
    position: TransparentGradientPosition = TransparentGradientPosition.Top,
    extraBottomPadding: Dp = 0.dp
) = run {
    Brush.verticalGradient(
        colorStops = position.colorStops,
        startY = 0f,
        endY = with(LocalDensity.current) {
            DisplayHeight.toPx() - SystemNavigationBarHeight.toPx()- BottomNavigationBarHeight.toPx() - extraBottomPadding.toPx()
        }
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
    TopAndBottom(
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

@Composable
fun LightFadeOut45Deg(canvasSize: Dp) = Brush.linearGradient(
    colors = listOf(Color.Black, Color.Transparent),
    start = Offset(0f, Float.POSITIVE_INFINITY),
    end = with(LocalDensity.current) { Offset(canvasSize.toPx() * 2f, 0f) }
)

@Preview
@Composable
fun TransparentGradientPreview() = PreviewComposable(enableDarkTheme = false) {

    val list = remember {
        buildList {
            add(Achievement("START", 0f))
            repeat(20) {
                addAll(
                    listOf(
                        Achievement("car$it", 1f, getTodaysDate()),
                        Achievement("big word big word$it", 0.5f, getTodaysDate()),
                        Achievement("bench$it", 0f)
                    )
                )
            }
            add(Achievement("END", 0f))
        }
    }

    Column(
        Modifier
            .fillMaxSize()
    ) {
        LazyColumn(Modifier.fadingEdge(TransparencyGradient(position = TransparentGradientPosition.TopAndBottom))){
            items(list, key = { it.objectName }) { achievement ->
                LightCircularContainer {
                    val rowEntry = StringBuilder(achievement.objectName.capitalized())
                    if (achievement.detectionDate != null){
                        rowEntry.append(" (${achievement.certaintyScore.toPercentage()})")
                    }
                    Text(rowEntry.toString())
                }
            }
        }
    }
}
