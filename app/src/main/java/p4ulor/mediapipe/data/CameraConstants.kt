package p4ulor.mediapipe.data

import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis

object CameraConstants {
    /** Specify to use back camera */
    val frontCamera = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    /**
     * Indicate the cameraProvider that we want to get extra details about the data
     * from the camera. Used to process the frames that are captured
     */
    val imageAnalyzerUseCase = ImageAnalysis.Builder()
        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .build()
}