package p4ulor.obj.detector.ui.screens.home.outline

import android.graphics.RectF
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import p4ulor.obj.detector.ui.animations.smooth

/** Utility class used for animating the bounds of a detection, by using the 4 corners of the box */
class AnimatedDetectionOutline(
    val xLeft: Animatable<Float, AnimationVector1D>,
    val yTop: Animatable<Float, AnimationVector1D>,
    val width: Animatable<Float, AnimationVector1D>,
    val height: Animatable<Float, AnimationVector1D>
) {
    /**
     * 4 coroutines are required so the 4 values are updated in parallel, since [animateTo] is a
     * suspend func. Otherwise, it will be visible how each value (and dimension) updates in steps
     */
    suspend fun updateBoundingBox(newBox: RectF) = withContext(Dispatchers.Default) {
        launch { xLeft.animateTo(newBox.left, smooth()) }
        launch { yTop.animateTo(newBox.top, smooth()) }
        launch { width.animateTo(newBox.width(), smooth()) }
        launch { height.animateTo(newBox.height(), smooth()) }
    }
}