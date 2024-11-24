package p4ulor.mediapipe.ui.utils

import androidx.compose.ui.geometry.Size
import p4ulor.mediapipe.i


/**
 * This function is used to calculate the size of a box (our camera preview) after scaling it
 * down to be fitted in a Container while preserving the aspect ration of the Box
 *
 * To fit the Box in the Container, we consider the aspect ratio (AR) of both
 * 1. Box AR is wider than Container:
 *      1. Set Box width equal to Container width
 *      2. Scale down height of Box to keep Box AR
 * 2. Box AR is taller than Container:
 *      1. Set Box height equal to Container height
 *      2. Scale down width of Box to keep Box AR
 */
fun getSizeOfBoxKeepingRatioGivenContainer(container: Size, box: Size) : Size {
    val boxRatio = box.width / box.height
    val containerRatio = container.width / container.height

    return if (boxRatio > containerRatio) {
        Size(
            width = container.width,
            height =  (container.width / box.width) * box.height,
        )
    } else {
        Size(
            width =  (container.height / box.height) * box.width,
            height = container.height,
        )
    }
}