package p4ulor.mediapipe.android.utils

import androidx.camera.core.AspectRatio
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.compose.ui.geometry.Size
import p4ulor.mediapipe.e


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
            width = (container.height / box.height) * box.width,
            height = container.height
        )
    }
}

fun ResolutionSelector.toSize() = when(this.aspectRatioStrategy){
    AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY -> Size(height = 4f, width = 3f)
    AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY -> Size(height = 16f, width = 9f)
    else -> Size(height = 16f, width = 9f).also { e("Unhandled case") }
}

fun ResolutionSelector.toInt() = when(this.aspectRatioStrategy){
    AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY -> AspectRatio.RATIO_4_3
    AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY -> AspectRatio.RATIO_16_9
    else -> AspectRatio.RATIO_16_9.also { e("Unhandled case") }
}

object CameraConstants {
    val RATIO_16_9 = ResolutionSelector.Builder().setAspectRatioStrategy(
        AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY
    ).build()

    val RATIO_4_3 = ResolutionSelector.Builder().setAspectRatioStrategy(
        AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
    ).build()

    fun ResolutionSelector.toggle() = when(aspectRatioStrategy){
        AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY -> RATIO_16_9
        AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY -> RATIO_4_3
        else -> { e("Unhandled case"); RATIO_16_9 }
    }
}
