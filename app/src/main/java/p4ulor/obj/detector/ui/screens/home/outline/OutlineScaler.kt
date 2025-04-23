package p4ulor.obj.detector.ui.screens.home.outline

import android.graphics.RectF
import androidx.compose.foundation.layout.BoxWithConstraints

/**
 * A utility class to handle the scaling of outlines (the bounding boxes used for object detections)
 * into a container, while keeping the aspect ratio of the original frame (camera ratio)
 *
 * @param frameWidth The width of the frame, captured by the camera
 * @param frameHeight The height of the frame, captured by the camera
 * @param containerWidth The width of the container where the outline will be drawn
 * @param containerHeight The height of the container where the outline will be drawn
 */
class OutlineScaler(
    private val frameWidth: Int,
    private val frameHeight: Int,
    private val containerWidth: Float,
    private val containerHeight: Float
) {
    /**
     * Scales the given bounding box dimensions and positions to fit inside a container, like a
     * [BoxWithConstraints].
     * @param bounds The original bounding box dimensions and position in the frame.
     * @return A [ScaledBox] containing the scaled width, height, left offset, and top offset.
     */
    fun scaleBox(bounds: RectF) = ScaledBox(
        xLeft = (bounds.left / frameWidth) * containerWidth,
        yTop = (bounds.top / frameHeight) * containerHeight,
        width = (bounds.width() / frameWidth) * containerWidth,
        height = (bounds.height() / frameHeight) * containerHeight,
    )

    fun scaleBox(bounds: AnimatedDetectionOutline) = scaleBox(
        RectF(
            bounds.xLeft.value,
            bounds.yTop.value,
            bounds.xLeft.value + bounds.width.value, //right
            bounds.yTop.value + bounds.height.value //bottom
        )
    )

    /** Data class representing the scaled box with its dimensions and position */
    data class ScaledBox(
        val xLeft: Float,
        val yTop: Float,
        val width: Float,
        val height: Float
    )
}
