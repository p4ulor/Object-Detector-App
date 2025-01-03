package p4ulor.mediapipe.data.domains.mediapipe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.ErrorListener
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import p4ulor.mediapipe.e
import p4ulor.mediapipe.i

/**
 * This class is the main class that uses the MediaPipe logic
 * https://ai.google.dev/edge/mediapipe/solutions/vision/object_detector/android
 */
class MyImageAnalyser(
    private val context: Context,
    private val settings: ObjectDetectorSettings = ObjectDetectorSettings()
) : ImageAnalysis.Analyzer {

    var callbacks: ObjectDetectorCallbacks? = null

    // Sample docs: For this example this needs to be a var so it can be reset on changes. If the ObjectDetector
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

    private fun resultListener() = MPImageResultListener { detectedObjects, inputImage ->
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - detectedObjects.timestampMs()
        if(callbacks==null){
            e("Callbacks is null, results will not be reported")
            return@MPImageResultListener
        }
        callbacks?.onResults(
            ResultBundle(
                detectedObjects,
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
     * Runs object detection on live streaming cameras frame-by-frame which are obtained and listened
     * to asynchronously through the [MPImageResultListener] set at [resultListener], which calls
     * the [callbacks] of this [MyImageAnalyser].
     */
    override fun analyze(image: ImageProxy) {
        require(settings.mediaTypeToAnalyze == RunningMode.LIVE_STREAM) {
            "This method can only be called in the context of a camera preview (live stream)"
        }

        val frameTime = SystemClock.uptimeMillis()

        // Copy out RGB bits from the frame to a bitmap buffer
        val bitmapBuffer = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

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
        objectDetector?.detectAsync(mpImage, frameTime) //todo, investigate com.google.mediapipe.framework.MediaPipeException: failed precondition: The received packets having a smaller timestamp than the processed timestamp
    }
}
