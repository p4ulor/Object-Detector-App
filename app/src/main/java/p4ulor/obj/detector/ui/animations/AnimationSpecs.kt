package p4ulor.obj.detector.ui.animations

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

/** [EaseInEaseOut](https://cubic-bezier.com/#.5,0,.5,1)*/
val EaseInEaseOut = CubicBezierEasing(.5f, 0f, .5f, 1f)
val Linear = CubicBezierEasing(0f, 0f, 1f, 1f)

fun <T> smooth(durationMillis: Int = 300, delayMillis: Int = 0) = tween<T>(
    durationMillis = durationMillis,
    delayMillis = delayMillis,
    easing = EaseInEaseOut
)

fun <T> linear(durationMillis: Int = 300, delayMillis: Int = 0) = tween<T>(
    durationMillis = durationMillis,
    delayMillis = delayMillis,
    easing = Linear
)

fun <T> bouncySpring() = spring<T>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)

fun <T> veryBouncySpring() = spring<T>(
    dampingRatio = Spring.DampingRatioHighBouncy,
    stiffness = Spring.StiffnessLow
)

fun slideInVertSmooth(slideUp: Boolean = true, durationMillis: Int = 400) = slideInVertically(
    animationSpec = smooth(
        durationMillis = durationMillis,
    ),
    initialOffsetY = { fullHeight -> fullHeight.handleSlide(slideUp) }
)

fun slideOutVertSmooth(slideUp: Boolean = true, durationMillis: Int = 400) = slideOutVertically(
    animationSpec = smooth(
        durationMillis = durationMillis,
    ),
    targetOffsetY = { fullHeight -> fullHeight.handleSlide(!slideUp) } // targetOffsetY essentially inverts the logic of this parameter, but to keep the caller of this function aware of the inversion between the in and out transition, we invert the value here, making it more intuitive to the caller
)

/** Handles either the initial or target height */
private fun Int.handleSlide(slideUp: Boolean) =
    if(slideUp) {
        this / 2
    } else {
        - this / 2
    }
