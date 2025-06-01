package p4ulor.obj.detector.ui.screens.achievements.leaderboard

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import p4ulor.obj.detector.data.utils.getOrRandom
import p4ulor.obj.detector.data.utils.toPercentage
import p4ulor.obj.detector.i
import p4ulor.obj.detector.ui.animations.linear
import p4ulor.obj.detector.ui.components.utils.CenteredColumn
import p4ulor.obj.detector.ui.components.utils.CenteredRow
import p4ulor.obj.detector.ui.components.utils.GeneralPaddingMedium
import p4ulor.obj.detector.ui.components.utils.HorizontalPadding
import p4ulor.obj.detector.ui.components.utils.VerticalPadding
import p4ulor.obj.detector.ui.theme.PreviewComposable
import kotlin.math.ceil

private const val MAX_ENTITIES_PER_ROW = 4

/**
 * @param data a list of [Triple]s where
 * 1. first = name of the entity
 * 2. second = it's value
 * 3. third = it's color (optional)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DonutChartWithLabels(
    data: List<Triple<String, Float, Color?>>,
    donutSize: Dp,
    modifier: Modifier = Modifier,
    donutStrokeWidth: Float = 50f,
) {

    val totalSum = rememberSaveable { data.sumOf { it.second.toDouble() }.toFloat() }

    val entities = remember {
        with(DonutChart) {
            data.take(MAX_ENTITIES).also {
                if (data.size > MAX_ENTITIES) {
                    i("Note: data size exceeds $MAX_ENTITIES")
                }
            }
        }
    }

    CenteredColumn(modifier) {
        Box(Modifier.size(donutSize).padding(GeneralPaddingMedium)) {
            DonutChart(
                data = entities,
                totalSum = totalSum,
                strokeWidth = donutStrokeWidth
            )
        }

        FlowRow( // items flow into the next line when the container runs out of space https://developer.android.com/develop/ui/compose/layouts/flow
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                space = HorizontalPadding,
                alignment = Alignment.CenterHorizontally
            ),
            verticalArrangement = Arrangement.spacedBy(VerticalPadding),
            maxItemsInEachRow = MAX_ENTITIES_PER_ROW,
        ) {
            entities.forEachIndexed { index, (label, value, color) ->
                val entityColor = color ?: DonutChart.predefinedColors.getOrRandom(index)
                CenteredRow(Modifier) {
                    Canvas(Modifier.size(16.dp)) {
                        drawCircle(entityColor)
                    }
                    Text(
                        "$label (${(value / totalSum).toPercentage()})",
                        fontSize = 12.sp,
                        softWrap = true
                    )
                }
            }
        }
    }
}

@Composable
fun DonutChart(
    data: List<Triple<String, Float, Color?>>,
    totalSum: Float,
    strokeWidth: Float = 50f,
) {

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
        data.forEachIndexed { index, (label, value, color) ->
            val targetAngle = (value / totalSum) * 360f
            val animatedTargetAngle = targetAngle * (animatedStartAngle.value / 360f)
            drawArc(
                color = color ?: DonutChart.predefinedColors.getOrRandom(index),
                startAngle = startAngle,
                sweepAngle = animatedTargetAngle - gapAngleDegrees,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
            startAngle += targetAngle
        }
    }
}

object DonutChart {
    val predefinedColors = listOf(
        Color(0xFF6A00B1),
        Color(0xFF00FF03),
        Color(0xFF1F4320),
        Color(0xAA4B95FF),
        Color(0xFFFF5100),
        Color(0xFFE30F57),
        Color(0xFFFFE800),
        Color(0xFFE100FF),
        Color(0xFFFFFFFF),
        Color(0xFF00FFD5),
        Color(0xFFB3FF00),
        Color(0xFF0000FF)
    )

    val MAX_ENTITIES = predefinedColors.size
}





@Preview
@Composable // Run in interactive mode
fun DonutChartExample() = PreviewComposable(enableDarkTheme = true) {
    DonutChartWithLabels(
        data = listOf(
            Triple("airplane", 50f, null),
            Triple("Apple", 15f, null),
            Triple("person", 15f, null),
            Triple("bicycle", 20f, null),
            Triple("car", 20f, null),
            Triple("cat", 20f, null)
        ),
        donutSize = 200.dp
    )
}

@Preview
@Composable // Run in interactive mode
fun DonutChartExample2() = PreviewComposable(enableDarkTheme = true) {
    DonutChartWithLabels(
        data = listOf(
            Triple("bicycle", 20f, null),
            Triple("car", 20f, null),
            Triple("cat", 20f, null),
            Triple("airplane", 50f, null),
            Triple("apple", 15f, null),
            Triple("person", 15f, null),
            Triple("backpack", 50f, null),
            Triple("sports ball", 15f, null),
            Triple("tennis racket", 15f, null),
        ),
        donutSize = 200.dp
    )
}
