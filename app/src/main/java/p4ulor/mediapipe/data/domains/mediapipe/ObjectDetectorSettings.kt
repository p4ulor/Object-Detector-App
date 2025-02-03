package p4ulor.mediapipe.data.domains.mediapipe

import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode

/**
 * A class that encapsulates the different settings that can be to detect objects using MediaPipe
 * @param [sensitivityThreshold] the minimum amount of certainty (percentage) of the image detector
 * in order to classify it as an object as X. Detections bellow this amount won't be displayed
 */
data class ObjectDetectorSettings(
    val processor: Delegate = Delegate.GPU,
    val maxObjectDetection: Int = ObjectDetectorSettings.maxObjectDetection,
    val mediaTypeToAnalyze: RunningMode = RunningMode.LIVE_STREAM,
    val model: Models = Models.EFFICIENTDETV0,
    val sensitivityThreshold: Float = 0.5F,
) {

    companion object {
        // Use later
        val detectionCertaintyRange = 0f..1f
        const val maxObjectDetection = 5
    }
}

/** These files should be in the assets folder */
enum class Models(val id: String) {
    EFFICIENTDETV0("efficientdet-lite0.tflite"),
    EFFICIENTDETV2("efficientdet-lite2.tflite")
}
