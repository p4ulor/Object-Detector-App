package p4ulor.mediapipe.data.utils

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.compose.ui.geometry.Size
import p4ulor.mediapipe.e

object CameraConstants {
    /** Specify to use back camera */
    val frontCamera = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    val RATIO_16_9 = ResolutionSelector.Builder().setAspectRatioStrategy(
        AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY
    ).build()

    val RATIO_4_3 = ResolutionSelector.Builder().setAspectRatioStrategy(
        AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
    ).build()
}
