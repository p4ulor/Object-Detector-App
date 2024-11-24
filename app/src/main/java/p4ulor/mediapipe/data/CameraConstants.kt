package p4ulor.mediapipe.data

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

    /** Get ratio in numerical format */
    fun ResolutionSelector.getRatio() = when(this.aspectRatioStrategy){
        AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY -> Size(height = 4f, width = 3f)
        AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY -> Size(height = 16f, width = 9f)
        else -> Size(height = 16f, width = 9f).also { e("Unhandled case") }
    }

    /**
     * Indicate the cameraProvider that we want to get extra details about the data
     * from the camera. Used to process the frames that are captured
     */
    val imageAnalyzerUseCase = ImageAnalysis.Builder()
        .setResolutionSelector(RATIO_4_3)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .build()
}
