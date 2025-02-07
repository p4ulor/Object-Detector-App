package p4ulor.mediapipe.android.viewmodels

import android.app.Application
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.sample
import p4ulor.mediapipe.android.utils.toStateFlow
import p4ulor.mediapipe.data.domains.mediapipe.MyImageAnalyser
import p4ulor.mediapipe.data.domains.mediapipe.ObjectDetectorCallbacks
import p4ulor.mediapipe.data.domains.mediapipe.ObjectDetectorSettings
import p4ulor.mediapipe.data.domains.mediapipe.ResultBundle
import p4ulor.mediapipe.data.sources.KtorClient
import p4ulor.mediapipe.data.storage.UserPreferences
import p4ulor.mediapipe.data.storage.dataStore
import p4ulor.mediapipe.data.utils.executor
import p4ulor.mediapipe.e

class MainViewModel(private val application: Application) : AndroidViewModel(application) {
    private val prefs = MutableStateFlow<UserPreferences?>(null)

    fun loadPrefs() = flow {
        prefs.value = UserPreferences.getFrom(application.applicationContext.dataStore)
        emit(prefs.value)
    }

    private val _objDetectionResults = MutableStateFlow<ResultBundle?>(null)
    @OptIn(FlowPreview::class)
    val objDetectionResults: StateFlow<ResultBundle?> get() = _objDetectionResults.let {
        if (prefs.value?.enableAnimations == true) it.sample(500L) else it
    }.toStateFlow(_objDetectionResults.value) // because [sample] returns a flow

    private val ktorClient = KtorClient("dummyjson.com")

    /**
     * Creates a [ImageAnalysis] analyzer with [MyImageAnalyser] and with [objectDetectorSettings]
     * The [imageAnalysisSettings] runs in a single thread pool [executor]
     */
    fun initObjectDetector(
        imageAnalysisSettings: ImageAnalysis,
        objectDetectorSettings: ObjectDetectorSettings = ObjectDetectorSettings()
    ): ImageAnalysis {

        val myImageAnalyser = MyImageAnalyser(application.applicationContext, objectDetectorSettings)
        myImageAnalyser.callbacks = object : ObjectDetectorCallbacks {
            override fun onResults(resultBundle: ResultBundle) {
                _objDetectionResults.value = resultBundle
            }

            override fun onError(error: String) {
                e("Error: $error")
            }
        }
        imageAnalysisSettings.setAnalyzer(
            executor,
            myImageAnalyser
        )
        return imageAnalysisSettings
    }
}
