package p4ulor.mediapipe.android.viewmodels

import android.app.Application
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.sample
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import p4ulor.mediapipe.android.utils.NetworkObserver
import p4ulor.mediapipe.android.utils.create
import p4ulor.mediapipe.android.utils.toStateFlow
import p4ulor.mediapipe.data.domains.mediapipe.MyImageAnalyser
import p4ulor.mediapipe.data.domains.mediapipe.ObjectDetectorCallbacks
import p4ulor.mediapipe.data.domains.mediapipe.ObjectDetectorSettings
import p4ulor.mediapipe.data.domains.mediapipe.ResultBundle
import p4ulor.mediapipe.data.sources.gemini.GeminiApiService
import p4ulor.mediapipe.data.storage.preferences.UserPreferences
import p4ulor.mediapipe.data.storage.preferences.UserSecretPreferences
import p4ulor.mediapipe.data.storage.preferences.dataStore
import p4ulor.mediapipe.data.storage.preferences.secretDataStore
import p4ulor.mediapipe.data.utils.executorForImgAnalysis
import p4ulor.mediapipe.e

/**
 * KoinComponent is used to inject [network] so it doesn't brake [create] at ViewModelFactory
 * - https://insert-koin.io/docs/reference/koin-core/koin-component/
 */
class HomeViewModel(private val application: Application) : AndroidViewModel(application), KoinComponent {
    val network: NetworkObserver by inject()

    private var geminiApi: GeminiApiService? = null

    private val prefs = MutableStateFlow(UserPreferences())
    private val secretPrefs = MutableStateFlow(UserSecretPreferences())

    private val _objDetectionResults = MutableStateFlow<ResultBundle?>(null)
    /** Contains the data necessary to outline an object into the screen */
    @OptIn(FlowPreview::class)
    val objDetectionResults: StateFlow<ResultBundle?> get() = _objDetectionResults.let {
        if (prefs.value.enableAnimations) it.sample(500L) else it
    }.toStateFlow(_objDetectionResults.value) // because [sample] returns a flow

    fun loadUserPrefs() = flow {
        prefs.value = UserPreferences.getFrom(application.applicationContext.dataStore)
        emit(prefs.value)
    }

    fun loadUserSecretPrefs() = flow {
        val obtainedPrefs = UserSecretPreferences.getFrom(application.applicationContext.secretDataStore)
        secretPrefs.value = obtainedPrefs
        if(obtainedPrefs.geminiApiKey.isNotBlank()){
            geminiApi = GeminiApiService(obtainedPrefs.geminiApiKey)
        }
        emit(secretPrefs.value)
    }

    /**
     * Creates a camera [ImageAnalysis] [UseCase] with [MyImageAnalyser] and with [objectDetectorSettings]
     * The [cameraImageAnalyser] runs in a single thread pool [executorForImgAnalysis]
     */
    fun initObjectDetector(
        cameraImageAnalyser: ImageAnalysis,
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
        cameraImageAnalyser.setAnalyzer(
            executorForImgAnalysis,
            myImageAnalyser
        )
        return cameraImageAnalyser
    }

    override fun onCleared() {
        geminiApi?.close()
    }
}
