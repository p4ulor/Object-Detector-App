package p4ulor.mediapipe.data.viewmodel

import android.app.Application
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.AndroidViewModel
import p4ulor.mediapipe.data.ObjectDetector
import java.util.concurrent.Executors

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val executors = Executors.newFixedThreadPool(3)
    private val objectDetector = ObjectDetector(application.applicationContext)

    fun process(imageAnalyzerUseCase: ImageAnalysis) {
        imageAnalyzerUseCase.setAnalyzer(
            executors,
            objectDetector
        )
    }
}
