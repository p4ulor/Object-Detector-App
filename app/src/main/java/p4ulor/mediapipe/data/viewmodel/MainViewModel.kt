package p4ulor.mediapipe.data.viewmodel

import android.app.Application
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.AndroidViewModel
import p4ulor.mediapipe.data.ObjectDetector
import p4ulor.mediapipe.data.ObjectDetectorCallbacks
import java.util.concurrent.Executors

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val executor = Executors.newFixedThreadPool(3)
    private val objectDetector = ObjectDetector(application.applicationContext)

    fun process(imageAnalyzerUseCase: ImageAnalysis, callbacks: ObjectDetectorCallbacks) {
        objectDetector.callbacks = callbacks
        imageAnalyzerUseCase.setAnalyzer(
            executor,
            objectDetector
        )
    }
}
