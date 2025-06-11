package p4ulor.obj.detector.data.domains.mediapipe

import android.content.Context
import androidx.annotation.StringRes
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import p4ulor.obj.detector.R
import p4ulor.obj.detector.data.sources.local.preferences.UserPreferences

/**
 * A class that encapsulates the different settings that can be to detect objects using MediaPipe
 * @param [sensitivityThreshold] the minimum amount of certainty (percentage) of the image detector
 * in order to classify it as an object as X. Detections bellow this amount won't be displayed
 */
data class ObjectDetectorSettings(
    val maxObjectDetections: Int = UserPreferences.Companion.Default.maxObjectsDetections,
    val sensitivityThreshold: Float = UserPreferences.Companion.Default.minDetectCertainty,
    val model: Model = UserPreferences.Companion.Default.selectedModel,
    val processor: Delegate = Delegate.GPU,
    val mediaTypeToAnalyze: RunningMode = RunningMode.LIVE_STREAM
) {

    companion object {
        val detectionCertaintyRange = 0f..1f
        const val maxObjectDetections = 5
    }
}

/**
 * These files should be in the assets folder with these names and file extension
 * - Model 0: accurate & lightweight. https://ai.google.dev/edge/mediapipe/solutions/vision/object_detector?hl=pt-br#efficientdet-lite0_model_recommended
 * - Model 2: more accurate but slower. https://ai.google.dev/edge/mediapipe/solutions/vision/object_detector?hl=pt-br#efficientdet-lite2_model
 */
enum class Model(val id: String, @StringRes val description: Int) {
    EFFICIENTDETV0("efficientdet-lite0.tflite", R.string.model0_description),
    EFFICIENTDETV2("efficientdet-lite2.tflite", R.string.model2_description);

    companion object {
        fun getFrom(prefs: UserPreferences) = Model.entries.firstOrNull {
            it.name == prefs.selectedModel
        } ?: EFFICIENTDETV0

        val modelNames = Model.entries.map { it.name }

        fun getModelsDescriptions(context: Context) = Model.entries.associate {
            it.name to "${it.name} (${context.getString(it.description)})"
        }

        fun indexOf(modelId: String) = Model.entries.indexOfFirst { modelId == it.name }
    }
}
