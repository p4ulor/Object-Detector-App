package p4ulor.mediapipe.ui.animations

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

/** [EaseInEaseOut](https://cubic-bezier.com/#.5,0,.5,1 )*/
val EaseInEaseOut = CubicBezierEasing(.5f, 0f, .5f, 1f)

fun<T> smooth(durationMillis: Int = 300) = tween<T>(
    durationMillis = durationMillis,
    easing = EaseInEaseOut
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

private fun Int.handleSlide(slideUp: Boolean) =
    if(slideUp) this / 2 else -this / 2
