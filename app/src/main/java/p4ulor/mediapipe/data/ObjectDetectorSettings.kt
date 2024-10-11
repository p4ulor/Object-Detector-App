package p4ulor.mediapipe.data

import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode

data class ObjectDetectorSettings(
    val processor: Delegate = Delegate.GPU,
    val maxObjectDetection: Int = 3,
    val mediaTypeToAnalyze: RunningMode = RunningMode.LIVE_STREAM,
    val model: Model = Model.EFFICIENTDETV0,
    val sensitivityThreshold: Float = 0.5F
) {

    companion object {
        // Use later
        val MAX_OBJECT_DETECTION = 10
        val MIN_OBJECT_DETECTION = 1
    }
}

enum class Model(name: String) {
    EFFICIENTDETV0("efficientdet-lite0.tflite"),
    EFFICIENTDETV2("efficientdet-lite2.tflite")
}
