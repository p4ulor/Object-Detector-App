package p4ulor.obj.detector.data.domains.mediapipe

import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.components.containers.Detection
import com.google.mediapipe.tasks.core.ErrorListener
import com.google.mediapipe.tasks.core.OutputHandler
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult

typealias MPImageResultListener = OutputHandler.ResultListener<ObjectDetectorResult, MPImage>

/**
 * Callbacks to expose callbacks set in [MyImageAnalyser] for [MPImageResultListener], which is set
 * at [ObjectDetector.ObjectDetectorOptions.Builder.setResultListener].
 * And for [ErrorListener] too.
 */
interface ObjectDetectorCallbacks {
    fun onResults(resultBundle: ResultBundle)
    fun onError(error: String)
}

/**
 * Contains the data necessary to outline an object into the screen.
 * Wraps results from inference, the time it takes for inference to be performed, and
 * the input image and height for properly scaling UI to return back to callers
 */
data class ResultBundle(
    val detectedObjects: ObjectDetectorResult,
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

/** Returns the  */
val Detection.certaintyScore: String
    get() = categories().first().score().toString().take(4)