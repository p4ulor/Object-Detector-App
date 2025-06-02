package p4ulor.obj.detector.ui.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay

@Composable
fun rememberBouncyFloatLooper(initialValue: Float, targetValue: Float): State<Float> {
    val animatedPointsY = remember { Animatable(initialValue) }

    LaunchedEffect(Unit) {
        while (true) {
            animatedPointsY.animateTo(
                targetValue = targetValue,
                animationSpec = linear(800)
            )
            animatedPointsY.animateTo(
                targetValue = initialValue+3f,
                animationSpec = smooth(),
            )
            animatedPointsY.animateTo(
                targetValue = initialValue,
                animationSpec = veryBouncySpring(),
            )
            delay(2000)
        }
    }

    return animatedPointsY.asState()
}