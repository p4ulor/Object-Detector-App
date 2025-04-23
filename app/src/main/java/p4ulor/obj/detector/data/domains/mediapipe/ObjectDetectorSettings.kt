package p4ulor.obj.detector.data.domains.mediapipe

import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import p4ulor.obj.detector.data.sources.local.preferences.UserPreferences

/**
 * A class that encapsulates the different settings that can be to detect objects using MediaPipe
 * @param [sensitivityThreshold] the minimum amount of certainty (percentage) of the image detector
 * in order to classify it as an object as X. Detections bellow this amount won't be displayed
 */
data class ObjectDetectorSettings(
    val maxObjectDetections: Int = Companion.maxObjectDetections,
    val sensitivityThreshold: Float = 0.5F,
    val model: Model = Model.EFFICIENTDETV0,
    val processor: Delegate = Delegate.GPU,
    val mediaTypeToAnalyze: RunningMode = RunningMode.LIVE_STREAM
) {

    companion object {
        // Use later
        val detectionCertaintyRange = 0f..1f
        const val maxObjectDetections = 5
    }
}

/** These files should be in the assets folder with these names and file extension */
enum class Model(val id: String) {
    EFFICIENTDETV0("efficientdet-lite0.tflite"),
    EFFICIENTDETV2("efficientdet-lite2.tflite");

    companion object {
        fun getFrom(prefs: UserPreferences) = Model.entries.firstOrNull {
            it.name == prefs.selectedModel
        } ?: EFFICIENTDETV0
    }
}
