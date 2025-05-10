package p4ulor.obj.detector.ui.screens.achievements.leaderboard

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import p4ulor.obj.detector.ui.animations.linear
import p4ulor.obj.detector.ui.components.utils.CenteredRow
import p4ulor.obj.detector.ui.components.utils.GeneralPaddingMedium
import p4ulor.obj.detector.ui.theme.PreviewComposable

/**
 * @param data a list of [Triple]s where
 * 1. first = name of the entity
 * 2. second = it's value
 * 3. third = it's color
 */
@Composable
fun DonutChartWithLabels(
    data: List<Triple<String, Float, Color>>,
    donutSize: Dp,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 50f,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(donutSize).padding(GeneralPaddingMedium)) {
            DonutChart(
                data = data,
                strokeWidth = strokeWidth
            )
        }

        CenteredRow(Modifier) {
            data.forEachIndexed { index, (label, value, color) ->
                CenteredRow(Modifier) {
                    Canvas(Modifier.size(16.dp)) {
                        drawCircle(color)
                    }
                    Text(text = label, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun DonutChart(
    data: List<Triple<String, Float, Color>>,
    strokeWidth: Float = 50f,
) {
    val totalSum = data.sumOf { it.second.toDouble() }.toFloat()
    val animatedStartAngle = remember { Animatable(0f) }
    val gapAngleDegrees = 15f // So the colors don't overlap due to using StrokeCap.Round

    LaunchedEffect(data) {
        animatedStartAngle.animateTo(
            targetValue = 360f,
            animationSpec = linear(500, delayMillis = 300)
        )
    }

    Canvas(Modifier.fillMaxSize().aspectRatio(1f)) {
        var startAngle = 0f
        data.forEach { (label, value, color) ->
            val targetAngle = (value / totalSum) * 360f
            val animatedTargetAngle = targetAngle * (animatedStartAngle.value / 360f)
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = animatedTargetAngle - gapAngleDegrees,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
            startAngle += targetAngle
        }
    }
}

@Preview
@Composable // Run in interactive mode
fun DonutChartExample() = PreviewComposable(enableDarkTheme = true) {
    DonutChartWithLabels(
        data = listOf(
            Triple("Pink", 50f, Color(0xFF9800FF)),
            Triple("2", 15f, Color(0xFF0AFF0D)),
            Triple("3", 15f, Color(0xFF4CAF50)),
            Triple("4", 20f, Color(0xAA0061FF))
        ),
        donutSize = 200.dp
    )
}
