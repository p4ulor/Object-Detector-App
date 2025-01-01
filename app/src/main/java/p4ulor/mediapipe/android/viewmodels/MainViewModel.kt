package p4ulor.mediapipe.android.viewmodels

import android.app.Application
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.sample
import p4ulor.mediapipe.data.domains.mediapipe.MyImageAnalyser
import p4ulor.mediapipe.data.domains.mediapipe.ObjectDetectorCallbacks
import p4ulor.mediapipe.data.domains.mediapipe.ObjectDetectorSettings
import p4ulor.mediapipe.data.domains.mediapipe.ResultBundle
import p4ulor.mediapipe.e
import p4ulor.mediapipe.toStateFlow
import java.util.concurrent.Executors

class MainViewModel(private val application: Application) : AndroidViewModel(application) {
    private val executor = Executors.newSingleThreadExecutor()

    private lateinit var imageAnalysisSettings: ImageAnalysis
    private lateinit var myImageAnalyser: MyImageAnalyser
    var animateResults = true

    private val _results = MutableStateFlow<ResultBundle?>(null)
    val results: StateFlow<ResultBundle?> get() = _results.let {
        if(animateResults) it.sample(500L) else it
    }.toStateFlow(_results.value)

    /**
     * Set's the [ImageAnalysis] analyzer with [MyImageAnalyser] (if it's confusing, it's the API
     * fault). And uses [objectDetectorSettings] when building [MyImageAnalyser]. The
     * [imageAnalysisSettings] runs in a single thread pool [executor]
     */
    fun initObjectDetector(
        imageAnalysisSettings: ImageAnalysis,
        objectDetectorSettings: ObjectDetectorSettings = ObjectDetectorSettings()
    ): ImageAnalysis {
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
            myImageAnalyser
        )
        return this.imageAnalysisSettings
    }
}
