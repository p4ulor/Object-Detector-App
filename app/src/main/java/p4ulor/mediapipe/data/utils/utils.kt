package p4ulor.mediapipe.data.utils

import androidx.camera.core.AspectRatio
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.compose.ui.geometry.Size
import p4ulor.mediapipe.e

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

/**
 * Indicate the cameraProvider that we want to get extra details about the data
 * from the camera. Used to process the frames that are captured
 */
fun imageAnalysisSettings(
    ratio: ResolutionSelector? = null,
    @AspectRatio.Ratio ratioDeprecated: Int? = null
) = ImageAnalysis.Builder().apply {
    if(ratio!=null) setResolutionSelector(ratio) // Using this was causing "java.lang.RuntimeException: Buffer not large enough for pixels" at bitmapBuffer.copyPixelsFromBuffer"
    else if(ratioDeprecated!=null) setTargetAspectRatio(ratioDeprecated)

    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
}.build()