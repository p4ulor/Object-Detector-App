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

fun slideInVertSmooth(durationMillis: Int = 400) = slideInVertically(
    animationSpec = smooth(
        durationMillis = durationMillis,
    )
)

fun slideOutVertSmooth(durationMillis: Int = 400) = slideOutVertically(
    animationSpec = smooth(
        durationMillis = durationMillis,
    )
)
