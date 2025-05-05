package p4ulor.obj.detector.ui.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun VerticallyAnimatedVisibility(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(smooth()) + expandVertically(
            animationSpec = smooth(),
            expandFrom = Alignment.Bottom
        ),
        exit = fadeOut(smooth()) + shrinkVertically(
            animationSpec = smooth(),
            shrinkTowards = Alignment.Top
        ),
        content = { content() }
    )
}
