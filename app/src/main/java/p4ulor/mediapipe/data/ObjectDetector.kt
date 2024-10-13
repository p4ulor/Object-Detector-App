package p4ulor.mediapipe.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.ErrorListener
import com.google.mediapipe.tasks.core.OutputHandler
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult
import p4ulor.mediapipe.e
import p4ulor.mediapipe.i

/**
 * Has all the MediaPipe logic
 */
class ObjectDetector(
    private val context: Context,
    private val settings: ObjectDetectorSettings = ObjectDetectorSettings()
) : ImageAnalysis.Analyzer {

    var callbacks: ObjectDetectorCallbacks? = null

    // For this example this needs to be a var so it can be reset on changes. If the ObjectDetector
    // will not change, a lazy val would be preferable.
    private var objectDetector: ObjectDetector? = null

    init {
        setupObjectDetector()
    }

    private fun setupObjectDetector() = with(settings){
        val mediaPipeBaseOptions = BaseOptions.builder()
            .setDelegate(processor)
            .setModelAssetPath(model.id)
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

        objectDetector = try {
            ObjectDetector.createFromOptions(context, objectDetectorOptions.build())
        } catch (e: Exception){
            i("Object detector exception $e")
            null
        }
    }

    private fun resultListener() = MPImageResultListener { objectResult, inputImage ->
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - objectResult.timestampMs()
        if(callbacks==null){
            e("Callbacks is null, results will not be reported")
            return@MPImageResultListener
        }
        callbacks?.onResults(
            ResultBundle(
                objectResult,
                inferenceTime,
                inputImage.height,
                inputImage.width
            )
        )
    }

    private fun errorListener() = ErrorListener {
        e("error $it")
    }

    /**
     * Runs object detection on live streaming cameras frame-by-frame and returns the results
     * asynchronously to the caller.
     */
    override fun analyze(image: ImageProxy) {
        require(settings.mediaTypeToAnalyze == RunningMode.LIVE_STREAM) {
            "This method can only be called in the context of a camera preview (live stream)"
        }

        val frameTime = SystemClock.uptimeMillis()

        // Copy out RGB bits from the frame to a bitmap buffer
        val bitmapBuffer = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)

        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
        image.close()
        // Rotate the frame received from the camera to be in the same direction as it'll be shown
        val matrix = Matrix().apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) }

        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer,
            0,
            0,
            bitmapBuffer.width,
            bitmapBuffer.height,
            matrix,
            true
        )

        // Convert the input Bitmap object to an MPImage object to run inference
        val mpImage = BitmapImageBuilder(rotatedBitmap).build()

        /**
         * Run object detection using MediaPipe Object Detector API
         * As we're using running mode LIVE_STREAM, the detection result will be returned in
         * [resultListener]
         */
        if (objectDetector==null){
            e("objectDetector is null!")
        }
        objectDetector?.detectAsync(mpImage, frameTime)
    }
}

private typealias MPImageResultListener = OutputHandler.ResultListener<ObjectDetectorResult, MPImage>

interface ObjectDetectorCallbacks {
    fun onResults(resultBundle: ResultBundle)
    fun onError(error: String)
}

// Wraps results from inference, the time it takes for inference to be performed, and
// the input image and height for properly scaling UI to return back to callers
data class ResultBundle(
    val result: ObjectDetectorResult,
    val processingTime: Long,
    val inputImageHeight: Int,
    val inputImageWidth: Int,
)