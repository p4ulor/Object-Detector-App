package p4ulor.mediapipe.android.utils

import androidx.camera.core.AspectRatio
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.UseCase
import androidx.camera.core.resolutionselector.ResolutionSelector

/**
 * Creates an [ImageAnalysis] object for a given resolution and format.
 * This builds a [UseCase] that's used with the [cameraProvider] and is used to process the frames
 * that are captured
 */
fun createImageAnalyser(
    @AspectRatio.Ratio ratioDeprecated: Int? = null,
    ratio: ResolutionSelector? = null
) = ImageAnalysis.Builder().apply {
    if(ratio!=null) {
        setResolutionSelector(ratio) // Using this was causing "java.lang.RuntimeException: Buffer not large enough for pixels" at bitmapBuffer.copyPixelsFromBuffer"
    }
    else if(ratioDeprecated!=null) {
        setTargetAspectRatio(ratioDeprecated)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
    } else {
        throw IllegalArgumentException("All arguments can't be null")
    }
}.build()
