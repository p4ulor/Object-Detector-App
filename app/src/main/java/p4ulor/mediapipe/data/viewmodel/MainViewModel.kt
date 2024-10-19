package p4ulor.mediapipe.data.viewmodel

import android.app.Application
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.AndroidViewModel
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import p4ulor.mediapipe.data.ObjectDetector
import p4ulor.mediapipe.data.ObjectDetectorCallbacks
import p4ulor.mediapipe.data.ResultBundle
import java.util.concurrent.Executors

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val executor = Executors.newSingleThreadExecutor()
    private val objectDetector = ObjectDetector(application.applicationContext)

    private val _objectResults = MutableStateFlow<ObjectDetectorResult?>(null)
    val objectResults: StateFlow<ObjectDetectorResult?> get() = _objectResults

    fun process(
        imageAnalyzerUseCase: ImageAnalysis,
    ) {
        objectDetector.callbacks = object : ObjectDetectorCallbacks {
            override fun onResults(resultBundle: ResultBundle) {
                _objectResults.value = resultBundle.result
            }

            override fun onError(error: String) {
                TODO("Not yet implemented")
            }
        }
        imageAnalyzerUseCase.setAnalyzer(
            executor,
            objectDetector //implements ImageAnalysis.Analyzer
        )
    }
}
