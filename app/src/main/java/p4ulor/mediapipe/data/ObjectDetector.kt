package p4ulor.mediapipe.data

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.ErrorListener
import com.google.mediapipe.tasks.core.OutputHandler
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult

/**
 * Has all the MediaPipe logic
 */
class ObjectDetector(
    private val context: Context,
    private val settings: ObjectDetectorSettings = ObjectDetectorSettings()
) : ImageAnalysis.Analyzer {

    // For this example this needs to be a var so it can be reset on changes. If the ObjectDetector
    // will not change, a lazy val would be preferable.
    private var objectDetector: ObjectDetector? = null

    init {
        setupObjectDetector()
    }

    private fun setupObjectDetector() = with(settings){
        val mediaPipeBaseOptions = BaseOptions.builder()
            .setDelegate(processor)
            .setModelAssetPath(model.name)
            .build()

        val objectDetectorOptions = ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(mediaPipeBaseOptions)
            .setScoreThreshold(sensitivityThreshold)
            .setRunningMode(mediaTypeToAnalyze)
            .setMaxResults(maxObjectDetection)

        when (mediaTypeToAnalyze) {
            RunningMode.IMAGE,
            RunningMode.VIDEO -> objectDetectorOptions.setRunningMode(mediaTypeToAnalyze)
            RunningMode.LIVE_STREAM ->
                objectDetectorOptions.setRunningMode(mediaTypeToAnalyze)
                    .setResultListener(resultListener())
                    .setErrorListener(errorListener())
        }

        objectDetector = ObjectDetector.createFromOptions(context, objectDetectorOptions.build())
    }

    private fun resultListener() = OutputHandler.ResultListener<ObjectDetectorResult, MPImage> { result, input ->

    }

    private fun errorListener() = ErrorListener {

    }

    /**
     * Runs object detection on live streaming cameras frame-by-frame and returns the results
     * asynchronously to the caller.
     */
    override fun analyze(image: ImageProxy) {
        require(settings.mediaTypeToAnalyze != RunningMode.LIVE_STREAM) {
            "This method can only be called in the context of a camera preview (live stream)"
        }
    }
}