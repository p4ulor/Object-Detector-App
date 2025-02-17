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
import p4ulor.mediapipe.android.utils.toStateFlow
import p4ulor.mediapipe.data.domains.mediapipe.MyImageAnalyser
import p4ulor.mediapipe.data.domains.mediapipe.ObjectDetectorCallbacks
import p4ulor.mediapipe.data.domains.mediapipe.ObjectDetectorSettings
import p4ulor.mediapipe.data.domains.mediapipe.ResultBundle
import p4ulor.mediapipe.data.sources.KtorClient
import p4ulor.mediapipe.data.storage.preferences.UserPreferences
import p4ulor.mediapipe.data.storage.preferences.dataStore
import p4ulor.mediapipe.data.utils.executorCommon
import p4ulor.mediapipe.e
import p4ulor.mediapipe.android.utils.create
import p4ulor.mediapipe.data.storage.preferences.UserSecretPreferences
import p4ulor.mediapipe.data.storage.preferences.secretDataStore
import p4ulor.mediapipe.data.utils.executorForImgAnalysis

/**
 * KoinComponent is used to inject [network] so it doesn't brake [create] at ViewModelFactory
 * - https://insert-koin.io/docs/reference/koin-core/koin-component/
 */
class MainViewModel(private val application: Application) : AndroidViewModel(application), KoinComponent {
    val network: NetworkObserver by inject()

    private val ktorClient = KtorClient("dummyjson.com")

    private val prefs = MutableStateFlow<UserPreferences?>(null)
    private val secretPrefs = MutableStateFlow<UserSecretPreferences?>(null)

    private val _objDetectionResults = MutableStateFlow<ResultBundle?>(null)
    @OptIn(FlowPreview::class)
    val objDetectionResults: StateFlow<ResultBundle?> get() = _objDetectionResults.let {
        if (prefs.value?.enableAnimations == true) it.sample(500L) else it
    }.toStateFlow(_objDetectionResults.value) // because [sample] returns a flow

    fun loadUserPrefs() = flow {
        prefs.value = UserPreferences.getFrom(application.applicationContext.dataStore)
        emit(prefs.value)
    }

    fun loadUserSecretPrefs() = flow {
        secretPrefs.value = UserSecretPreferences.getFrom(application.applicationContext.secretDataStore)
        emit(secretPrefs.value)
    }

    /**
     * Creates a [ImageAnalysis] analyzer with [MyImageAnalyser] and with [objectDetectorSettings]
     * The [imageAnalysisSettings] runs in a single thread pool [executorCommon]
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
            executorForImgAnalysis,
            myImageAnalyser
        )
        return imageAnalysisSettings
    }
}
