package p4ulor.obj.detector.android.viewmodels

import android.app.Application
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.lifecycle.AndroidViewModel
import com.google.mediapipe.tasks.components.containers.Detection
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import p4ulor.obj.detector.R
import p4ulor.obj.detector.android.MyApplication
import p4ulor.obj.detector.android.utils.NetworkObserver
import p4ulor.obj.detector.android.utils.NotificationManager
import p4ulor.obj.detector.android.utils.camera.CameraConstants
import p4ulor.obj.detector.android.utils.camera.CameraConstants.toggle
import p4ulor.obj.detector.android.utils.camera.Picture
import p4ulor.obj.detector.android.utils.camera.takePic
import p4ulor.obj.detector.android.viewmodels.utils.launch
import p4ulor.obj.detector.android.viewmodels.utils.toStateFlow
import p4ulor.obj.detector.data.domains.gemini.GeminiPrompt
import p4ulor.obj.detector.data.domains.gemini.GeminiStatus
import p4ulor.obj.detector.data.domains.mediapipe.MyImageAnalyser
import p4ulor.obj.detector.data.domains.mediapipe.ObjectDetectorCallbacks
import p4ulor.obj.detector.data.domains.mediapipe.ObjectDetectorSettings
import p4ulor.obj.detector.data.domains.mediapipe.ResultBundle
import p4ulor.obj.detector.data.domains.mediapipe.certaintyScore
import p4ulor.obj.detector.data.domains.mediapipe.objectName
import p4ulor.obj.detector.data.sources.cloud.gemini.GeminiApiService
import p4ulor.obj.detector.data.sources.local.database.achievements.AchievementsTuple
import p4ulor.obj.detector.data.sources.local.preferences.UserPreferences
import p4ulor.obj.detector.data.sources.local.preferences.UserSecretPreferences
import p4ulor.obj.detector.data.sources.local.preferences.dataStore
import p4ulor.obj.detector.data.sources.local.preferences.secretDataStore
import p4ulor.obj.detector.data.utils.executorForImgAnalysis
import p4ulor.obj.detector.e
import p4ulor.obj.detector.ui.screens.home.chat.GeminiChatContainer
import p4ulor.obj.detector.ui.screens.home.chat.Message
import p4ulor.obj.detector.ui.screens.home.outline.AnimatedDetectionOutline
import java.time.Instant
import java.util.Date

/**
 * A manually injected view model for demo/historical purposes, which consequently demonstrates the
 * use of [KoinComponent], which is used to inject [network] and [notificationManager].
 * - https://insert-koin.io/docs/reference/koin-core/koin-component/
 * I'm using AndroidViewModel here in order to have the [application] which is used for
 * [achievementsDao], [initObjectDetector], [loadAndGetUserPrefs] and [promptGemini]
 *
 * This class has some data that should survive recompositions for a good UX. These are:
 * - [cameraPreviewRatio], [pictureTaken], [isGeminiEnabled], [geminiStatus], [geminiMessage] etc
 *
 * It also handles some logic that should not be delegated to the UI and logic that uses both data
 * and UI components. These operations are: handling connection losses, performing
 * async calls, loading user preferences, launching coroutines and accessing other data components.
 * Note: [isGeminiEnabled] is also used to toggle on/off emissions of [_objDetectionResults] used
 * for MediaPipe (and thus the detection outlines)
 */
class HomeViewModel(private val application: Application) : AndroidViewModel(application), KoinComponent {
    private val network: NetworkObserver by inject()
    private val notificationManager: NotificationManager by inject()

    private val achievementsDao by lazy {
        getApplication<MyApplication>().appDb.achievements()
    }

    private val newObjectsAchieved = mutableListOf<String>()
    private val allUnreachedAchievements = mutableListOf<AchievementsTuple>()

    private val _cameraPreviewRatio = MutableStateFlow(CameraConstants.RATIO_16_9)
    val cameraPreviewRatio = _cameraPreviewRatio.asStateFlow()

    private val _pictureTaken = MutableStateFlow<Picture?>(null)
    val pictureTaken = _pictureTaken.asStateFlow()

    private var geminiApi: GeminiApiService? = null

    private val _geminiStatus = MutableStateFlow(GeminiStatus.OFF)
    val geminiStatus = _geminiStatus.asStateFlow()

    private val _geminiMessage = MutableStateFlow(Message.getBlank)
    val geminiMessage = _geminiMessage.asStateFlow()

    private var prefs = UserPreferences()
    private var secretPrefs = UserSecretPreferences()

    /**
     * For [objDetectionResults], [toStateFlow] is used instead of [asStateFlow] because [sample]
     * returns a flow. When animations are enabled, the emissions are cut down to 1 every half a
     * second so that [AnimatedDetectionOutline] doesn't have a ton of work to do, otherwise
     * no animation would be visible since there would be too much lag when performing the animation
     * calculations each 30fps.
     */
    private val _objDetectionResults = MutableStateFlow<ResultBundle?>(null)
    @OptIn(FlowPreview::class)
    val objDetectionResults: StateFlow<ResultBundle?> get() = _objDetectionResults.let {
        if (prefs.enableAnimations) it.sample(500L) else it
    }.toStateFlow(_objDetectionResults.value)

    init { // Since geminiStatus needs to be persisted, it is managed like this, contrary to the use of hasConnection in SettingsScreen
        launch {
            network.hasConnection.collect {
                handleGeminiOnNetworkChange(it)
            }
        }

        // A job that contains scheduled notifications to send, this can be cancelled when a new scheduling comes in
        var sendNotificationJob = Job().apply { complete() }.job

        launch {
            objDetectionResults.collect { results ->
                results?.detectedObjects?.detections()?.let { detections ->
                    if(detections.isNotEmpty()){
                        checkForNewAchievements(detections)
                        if(newObjectsAchieved.isNotEmpty()){
                            if(sendNotificationJob.isActive){
                                sendNotificationJob.cancel()
                            }
                            sendNotificationJob = launch {
                                delay(5000)
                                notificationManager.sendAchievementNotification(newObjects = newObjectsAchieved)
                                newObjectsAchieved.clear()
                            }
                        }
                    }
                }
            }
        }
    }

    fun takePicture(imageCaptureUseCase: ImageCapture) {
        imageCaptureUseCase.takePic(application, saveInStorage = prefs.savePictures) { picture ->
            _pictureTaken.value = picture
        }
    }

    fun toggleCameraPreviewRatio(): ResolutionSelector {
        _cameraPreviewRatio.value = _cameraPreviewRatio.value.toggle()
        return _cameraPreviewRatio.value
    }

    fun loadAndGetUserPrefs() = flow {
        prefs = UserPreferences.getFrom(application.applicationContext.dataStore)
        emit(prefs)
    }

    fun loadUserSecretPrefs() {
        launch {
            val obtainedPrefs = UserSecretPreferences.getFrom(application.applicationContext.secretDataStore)
            secretPrefs = obtainedPrefs
            geminiApi = if(obtainedPrefs.geminiApiKey.isNotBlank()){
                GeminiApiService(obtainedPrefs.geminiApiKey)
            } else {
                null
            }
        }
    }

    fun loadAchievements() {
        launch {
            allUnreachedAchievements.clear()
            allUnreachedAchievements.addAll(achievementsDao.getAllUnreachedAchievements())
        }
    }

    /**
     * Creates a camera [ImageAnalysis] [UseCase] with [MyImageAnalyser] and with [objectDetectorSettings]
     * The [cameraImageAnalyser] runs in a single thread pool [executorForImgAnalysis]
     */
    fun initObjectDetector(
        cameraImageAnalyser: ImageAnalysis,
        objectDetectorSettings: ObjectDetectorSettings = ObjectDetectorSettings()
    ): ImageAnalysis {
        val myImageAnalyser = MyImageAnalyser(
            application.applicationContext,
            objectDetectorSettings,
            resultCallback = object : ObjectDetectorCallbacks {
                override fun onResults(resultBundle: ResultBundle) {
                    if (!_geminiStatus.value.isEnabled) {
                        _objDetectionResults.value = resultBundle
                    }
                }

                override fun onError(error: String) {
                    e("Error: $error")
                }
            }
        )

        cameraImageAnalyser.setAnalyzer(
            executorForImgAnalysis,
            myImageAnalyser
        )
        return cameraImageAnalyser
    }

    private suspend fun handleGeminiOnNetworkChange(hasConnection: Boolean) {
        if (!hasConnection){
            if(_geminiStatus.value.isEnabled){
                _geminiStatus.value = GeminiStatus.DISCONNECTED
                delay(300) // Give some time for the disconnection event to be transmitted and valid. This helps in showing the "Connection lost" toast only in HomeScreenGranted, since it will capture the DISCONNECTED value (and to avoid showing the toast when per example going from Settings to Home). This way, there's no need to add logic in HomeScreenGranted to save the previous status and decide to show the "Connection lost" toast. It may seem I'm doing work for the UI but the truth is that after something is disconnected, it's turned off, so it's acceptable
            }
            _geminiStatus.value = GeminiStatus.OFF
        }
    }

    /**
     * @param onFail is called if there are no conditions to turn on
     * Gemini (no connection or the loaded [loadUserSecretPrefs] were not performed or are not valid)
     */
    fun toggleGemini(onFail: () -> Unit) {
        launch {
            val hasConnection = runCatching {
                withTimeout(100L){ // In case there were no emissions to hasConnection yet
                    network.hasConnection.first()
                }
            }.getOrNull() ?: false
            if (hasConnection && secretPrefs.isValid && geminiApi != null) {
                _geminiStatus.value = _geminiStatus.value.toggle()
            } else {
                onFail()
            }
        }
    }

    /**
     * Prompts Gemini with a [GeminiPrompt] if the [geminiApi] is initialized and [pictureTaken]
     * is not null. Even thought [GeminiChatContainer] should disable the prompt submission, but
     * we check here to avoid any problems
     */
    fun promptGemini(prompt: String): Unit = with(application.applicationContext){
        if (geminiApi != null) {
            launch {
                val geminiPrompt = pictureTaken.value?.imageAsBase64()?.let { GeminiPrompt(prompt, it) }
                if (geminiPrompt != null) {
                    _geminiMessage.value = Message.getPending
                    val response = Message.from(resp = geminiApi?.promptWithImage(geminiPrompt))
                    if (response != null) {
                        _geminiMessage.value = response
                    } else {
                        _geminiMessage.value = Message.createGeminiMessage(
                            getString(R.string.internal_gemini_api_error)
                        )
                    }
                    _pictureTaken.value = null
                } else {
                    _geminiMessage.value = Message.createGeminiMessage(
                        getString(R.string.error_creating_gemini_prompt)
                    )
                    e("geminiPrompt is null")
                }
            }
        } else {
            e("geminiApi is null")
        }
    }

    override fun onCleared() {
        geminiApi?.close()
    }

    private suspend fun checkForNewAchievements(objectsDetected: MutableList<Detection>) {
        objectsDetected.forEach { obj -> // Go through the (definitely) smaller list
            val iterator = allUnreachedAchievements.iterator() // Go through the unreached achievements and see if the detected object is in this list. The iterator is used to avoid ConcurrentModificationException. An iterator can't be reset so it must be created here everytime
            while (iterator.hasNext()) {
                val unreached = iterator.next()
                if (obj.objectName.equals(unreached.objectName, ignoreCase = true)) {
                    newObjectsAchieved.add(unreached.objectName)
                    achievementsDao.insert(
                        AchievementsTuple(
                            obj.objectName,
                            obj.certaintyScore.toFloatOrNull() ?: 0f,
                            Date.from(Instant.now())
                        )
                    )
                    iterator.remove() // Removes the latest value returned by next() from the collection of the iterator (allUnreachedAchievements).
                }
            }
        }
    }
}
