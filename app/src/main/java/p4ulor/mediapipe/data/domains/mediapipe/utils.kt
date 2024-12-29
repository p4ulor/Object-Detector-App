package p4ulor.mediapipe.data.domains.mediapipe

import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.components.containers.Detection
import com.google.mediapipe.tasks.core.OutputHandler
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult

typealias MPImageResultListener = OutputHandler.ResultListener<ObjectDetectorResult, MPImage>

interface ObjectDetectorCallbacks {
    fun onResults(resultBundle: ResultBundle)
    fun onError(error: String)
}

/**
 * Wraps results from inference, the time it takes for inference to be performed, and
 * the input image and height for properly scaling UI to return back to callers
 */
data class ResultBundle(
    val result: ObjectDetectorResult,
    val processingTimeMs: Long,
    val inputImageHeight: Int,
    val inputImageWidth: Int,
)

/**
 * The name of the detected object
 * It's not clear why categories() is a list
 * https://ai.google.dev/edge/api/mediapipe/python/mp/tasks/components/containers/Detection
 */
val Detection.objectName: String
    get() = categories().first().categoryName()