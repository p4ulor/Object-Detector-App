package p4ulor.mediapipe.data.viewmodel

import android.app.Application
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import p4ulor.mediapipe.data.MyImageAnalyser
import p4ulor.mediapipe.data.ObjectDetectorCallbacks
import p4ulor.mediapipe.data.ObjectDetectorSettings
import p4ulor.mediapipe.data.ResultBundle
import p4ulor.mediapipe.toStateFlow
import java.util.concurrent.Executors

class MainViewModel(private val application: Application) : AndroidViewModel(application) {
    private val executor = Executors.newSingleThreadExecutor()

    lateinit var imageAnalysisSettings: ImageAnalysis
    private lateinit var myImageAnalyser: MyImageAnalyser

    private val _results = MutableStateFlow<ResultBundle?>(null)
    val results: StateFlow<ResultBundle?> get() = _results.sample(1000L).toStateFlow(_results.value)

    /** For ou can use [p4ulor.mediapipe.data.utils.imageAnalyzer] */
    fun initObjectDetector(
        imageAnalysisSettings: ImageAnalysis,
        objectDetectorSettings: ObjectDetectorSettings = ObjectDetectorSettings()
    ) {
        this.imageAnalysisSettings = imageAnalysisSettings
        myImageAnalyser = MyImageAnalyser(application.applicationContext, objectDetectorSettings)
        myImageAnalyser.callbacks = object : ObjectDetectorCallbacks {
            override fun onResults(resultBundle: ResultBundle) {
                _results.value = resultBundle
            }

            override fun onError(error: String) {
                TODO("Not yet implemented")
            }
        }
        imageAnalysisSettings.setAnalyzer(
            executor,
            myImageAnalyser //implements ImageAnalysis.Analyzer
        )
    }
}
