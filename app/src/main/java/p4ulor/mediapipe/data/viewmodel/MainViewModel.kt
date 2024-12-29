package p4ulor.mediapipe.data.viewmodel

import android.app.Application
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.transformLatest
import p4ulor.mediapipe.data.MyImageAnalyser
import p4ulor.mediapipe.data.ObjectDetectorCallbacks
import p4ulor.mediapipe.data.ObjectDetectorSettings
import p4ulor.mediapipe.data.ResultBundle
import p4ulor.mediapipe.e
import p4ulor.mediapipe.toStateFlow
import java.util.concurrent.Executors

class MainViewModel(private val application: Application) : AndroidViewModel(application) {
    private val executor = Executors.newSingleThreadExecutor()

    lateinit var imageAnalysisSettings: ImageAnalysis
    private lateinit var myImageAnalyser: MyImageAnalyser
    var animateResults = true

    private val _results = MutableStateFlow<ResultBundle?>(null)
    val results: StateFlow<ResultBundle?> get() = _results.let {
        if(animateResults) it.sample(500L) else it
    }.toStateFlow(_results.value)

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
                e("Error: $error")
            }
        }
        imageAnalysisSettings.setAnalyzer(
            executor,
            myImageAnalyser //implements ImageAnalysis.Analyzer
        )
    }
}
