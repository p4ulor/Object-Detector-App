package p4ulor.obj.detector.ui.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import p4ulor.obj.detector.R
import p4ulor.obj.detector.android.utils.camera.Picture
import p4ulor.obj.detector.android.utils.camera.createCameraImageAnalyser
import p4ulor.obj.detector.android.utils.camera.createImageCaptureUseCase
import p4ulor.obj.detector.android.utils.camera.getSizeOfBoxKeepingRatioGivenContainer
import p4ulor.obj.detector.android.utils.camera.hasFlash
import p4ulor.obj.detector.android.utils.camera.is4by3
import p4ulor.obj.detector.android.utils.camera.isHdrSupported
import p4ulor.obj.detector.android.utils.camera.toInt
import p4ulor.obj.detector.android.utils.camera.toSize
import p4ulor.obj.detector.android.utils.camera.toggleFlash
import p4ulor.obj.detector.android.viewmodels.HomeViewModel
import p4ulor.obj.detector.data.domains.gemini.GeminiStatus
import p4ulor.obj.detector.data.domains.mediapipe.Model
import p4ulor.obj.detector.data.domains.mediapipe.ObjectDetectorSettings
import p4ulor.obj.detector.data.sources.local.preferences.UserPreferences
import p4ulor.obj.detector.e
import p4ulor.obj.detector.i
import p4ulor.obj.detector.ui.components.ExpandableFAB
import p4ulor.obj.detector.ui.components.FloatingActionButton
import p4ulor.obj.detector.ui.components.Icon
import p4ulor.obj.detector.ui.components.MaterialIcons
import p4ulor.obj.detector.ui.components.ResourcesIcon
import p4ulor.obj.detector.ui.components.utils.ScreenHeight
import p4ulor.obj.detector.ui.components.utils.toast
import p4ulor.obj.detector.ui.screens.home.chat.GeminiChatContainer
import p4ulor.obj.detector.ui.screens.home.outline.ObjectBoundsBoxOutlines

/**
 * This function has a lot of nested calls, but in the way it's now, it's more readable because it's
 * more direct. Breaking it down into more util functions would make it cluttered even more, give
 * the amount of common variables each component uses
 *
 * We are using CameraProvider and not CameraController for more customization
 * https://developer.android.com/media/camera/camerax/architecture#cameraprovider
 *
 * Unfortunately, I haven't found a way to fix the lag or provide a loading animation when toggling
 * camera ratios
 *
 * @param prefs is required so that the [AndroidView] wouldn't have needed to re-created with a new camera
 * just because it's waiting for the [prefs] to be loaded and emitted. Unlike other data like the
 * UserSecretPrefs and the Achievements, which are loaded upon the first creation of the composable
 */
@Composable
fun HomeScreenGranted(
    vm: HomeViewModel,
    cameraProvider: ProcessCameraProvider,
    prefs: UserPreferences
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val ctx = LocalContext.current

    // Camera
    var camera by remember { mutableStateOf<Camera?>(null) } /** this is initialized in [startCameraAndPreviewView] */
    var isFlashEnabled by rememberSaveable { mutableStateOf(false) }
    val cameraPreviewRatio by vm.cameraPreviewRatio.collectAsState()
    var imageCaptureUseCase by remember { mutableStateOf(createImageCaptureUseCase(cameraPreviewRatio)) }
    val pictureTaken by vm.pictureTaken.collectAsState()
    var isAppMinimized by rememberSaveable { mutableStateOf(false) }

    // Gemini
    val geminiStatus by vm.geminiStatus.collectAsState()
    val geminiMessage by vm.geminiMessage.collectAsState()
    /**
     * Doesn't require storage permissions, it's compatible from Android 11 (API level 30) up
     * https://developer.android.com/training/data-storage/shared/photopicker
     */
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            i("Obtained picture $uri")
            vm.setPicture(Picture.File(uri, wasImported = true))
        } else {
            e("Launcher exited or picture is invalid")
        }
    }

    // MediaPipe
    val resultsBundle by vm.objDetectionResults.collectAsState()

    CameraUseBinder(
        lifecycleOwner,
        onBind = {
            i("Will re-bind camera")
            isAppMinimized = false
        },
        onUnbind = {
            i("Unbinding camera")
            cameraProvider.unbindAll()
            camera = null
            isAppMinimized = true
            isFlashEnabled = false
        }
    )

    LaunchedEffect(Unit) {
        vm.loadUserSecretPrefs()
        vm.loadAchievements()
    }

    LaunchedEffect(geminiStatus) {
        if (geminiStatus.isDisconnected) {
            ctx.toast(R.string.connection_lost)
        }
    }

    if(!isAppMinimized) { // Avoids showing composables of this screen for some milliseconds when changing screens, the justification is that the camera and MediaPipe uses a lot of resources. And this is also used to terminate the PreviewView, in order to avoid an occasional log spam updateSurface: surface is not valid when the app is minimized. (Apparently this is the only way by indicating to cancel the render of the AndroidView in the compose tree)
        Box(Modifier.fillMaxSize()) {
            val (modifier, alignment) = modifierAndAlignmentFor(cameraPreviewRatio, geminiStatus)

            BoxWithConstraints(modifier, alignment) {
                val cameraPreviewSize = getSizeOfBoxKeepingRatioGivenContainer(
                    container = with(this@BoxWithConstraints) {
                        Size(width = maxWidth.value, height = maxHeight.value)
                    },
                    box = with(cameraPreviewRatio.toSize()) {
                        Size(width = width, height = height)
                    }
                )

                EdgeBars(cameraPreviewSize, alignment)

                Box(
                    Modifier
                        .width(cameraPreviewSize.width.dp)
                        .height(cameraPreviewSize.height.dp)
                ) {
                    key(imageCaptureUseCase) { // Only re-create this view when imageCaptureUseCase updates (due to a camera ratio toggle)
                        AndroidView( // CameraX isn't providing a composable yet for Camera Preview, so we use AndroidView to use it
                            modifier = Modifier.fillMaxSize(),
                            factory = { ctx -> // The following operations should be done in the main thread and are expensive, which can result in Choreographer complaining with "Skipped 42~ frames". Opening the camera with these use cases and using an AndroidView, may explain this, so maybe this can't be avoided
                                val cameraPreviewView = PreviewView(ctx)

                                val imageAnalysisSettings = vm.initObjectDetector(
                                    createCameraImageAnalyser(cameraPreviewRatio.toInt()),
                                    ObjectDetectorSettings(
                                        maxObjectDetections = prefs.maxObjectDetections,
                                        sensitivityThreshold = prefs.minDetectCertainty,
                                        model = Model.getFrom(prefs)
                                    )
                                )

                                camera = startCameraAndPreviewView(
                                    cameraProvider,
                                    cameraPreviewView,
                                    imageCaptureUseCase,
                                    imageAnalysisSettings,
                                    lifecycleOwner
                                )

                                cameraPreviewView
                            }
                        )
                    }

                    // Show the detected objects outlines
                    resultsBundle?.let {
                        if(!geminiStatus.isEnabled){ // Easy implementation instead of rebinding the camera just for toggling off ImageAnalysis (there are other things to do in the project). We only re-bind the camera during app use only when the ratio changes, which is the most important to be able to have a valid ImageCapture to take pics after the fact
                            ObjectBoundsBoxOutlines(
                                detections = it.detectedObjects.detections(),
                                frameWidth = it.inputImageWidth,
                                frameHeight = it.inputImageHeight,
                                animate = prefs.enableAnimations
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = geminiStatus.isEnabled, Modifier.fillMaxSize()) {
                GeminiChatContainer(
                    modifierSizeFor(cameraPreviewRatio, geminiStatus),
                    newGeminiMessage = geminiMessage,
                    pictureTaken = pictureTaken,
                    onValidUserSubmit = { text ->
                        i("Prompting Gemini with: $text")
                        vm.promptGemini(text)
                    }
                )
            }
        }

        ExpandableFAB(
            listOpenerFAB = FloatingActionButton(Icon.Material(MaterialIcons.Add)),
            fabs = buildList {
                add(
                    FloatingActionButton(Icon.App(ResourcesIcon.Camera)) {
                        vm.takePicture(imageCaptureUseCase)
                    }
                )
                add(
                    if(geminiStatus.isEnabled){
                        FloatingActionButton(Icon.App(ResourcesIcon.MediaPipe)){
                            vm.toggleGemini(onFail = {})
                        }
                    } else {
                        FloatingActionButton(Icon.App(ResourcesIcon.Gemini)) {
                            vm.toggleGemini(onFail = {
                                ctx.toast(R.string.check_internet_and_gemini_key)
                            })
                        }
                    }
                )
                add(
                    FloatingActionButton(Icon.App(ResourcesIcon.Scale)) {
                        cameraProvider.unbindAll() // because a new [camera] will be initialized in the AndroidView
                        imageCaptureUseCase = createImageCaptureUseCase(vm.toggleCameraPreviewRatio())
                    }
                )

                add(
                    FloatingActionButton(
                        Icon.App(
                        if (isFlashEnabled) ResourcesIcon.FlashlightOff else ResourcesIcon.FlashlightOn
                    )) {
                        isFlashEnabled = !isFlashEnabled
                        camera?.apply {
                            if (hasFlash) {
                                i("Torch supported, state: ${cameraInfo.torchState.value}")
                                toggleFlash(isFlashEnabled)
                            } else {
                                i("Torch not supported")
                            }
                        }
                    }
                )
                add(
                    FloatingActionButton(Icon.Material(MaterialIcons.PermMedia)) {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }
        )
    }
}

/**
 * Creates a camera [PreviewView] which is passed to the [cameraProvider] along with other
 * [UseCase]s (max 3), that are used with the camera
 * @return The created [Camera]
 */
@MainThread
private fun startCameraAndPreviewView(
    cameraProvider: ProcessCameraProvider,
    cameraPreviewView: PreviewView,
    imageCaptureUseCase: ImageCapture,
    imageAnalysisSettings: ImageAnalysis,
    lifecycleOwner: LifecycleOwner
): Camera {

    // Create a Preview (UseCase) to tell the cameraProvider that we want to preview the camera
    val previewUseCase = Preview.Builder().apply {
        cameraProvider.isHdrSupported?.let { setDynamicRange(it) }
    }.build().apply {
        surfaceProvider = cameraPreviewView.surfaceProvider
    }

    return cameraProvider.bindToLifecycle( // Requires main thread
        lifecycleOwner,
        CameraSelector.DEFAULT_BACK_CAMERA,
        useCases = arrayOf(
            previewUseCase,
            imageCaptureUseCase,
            imageAnalysisSettings
        )
    )
}

/** Add bars in the sides so the background doesn't show, per example, when camera ratio == 4:3 */
@Composable
private fun BoxWithConstraintsScope.EdgeBars(cameraPreviewSize: Size, aligntment: Alignment) {
    Box(
        Modifier
            .align(aligntment)
            .fillMaxWidth()
            .height(cameraPreviewSize.height.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
    )
}

/**
 * Specifies the right modifier and alignment for 4_3 and 16_9 camera preview ratios
 * - 4_3 -> Top with height equal to half of the available height
 * - 16_9 -> Bottom with max size
 */
@Composable
private fun modifierAndAlignmentFor(
    cameraPreviewRatio: ResolutionSelector,
    geminiStatus: GeminiStatus
): Pair<Modifier, Alignment> = Pair(
    first = modifierSizeFor(cameraPreviewRatio, geminiStatus),
    second = if (cameraPreviewRatio.is4by3) {
        Alignment.TopCenter
    } else {
        Alignment.BottomCenter
    }
)

/** Note: this is used for both the box with the camera & detection overlays and [GeminiChatContainer] */
@Composable
private fun modifierSizeFor(cameraPreviewRatio: ResolutionSelector, geminiStatus: GeminiStatus) =
    if (cameraPreviewRatio.is4by3) {
        Modifier
            .fillMaxWidth()
            .then(
                if(geminiStatus.isEnabled){
                    Modifier.height(ScreenHeight / 2)
                } else {
                    Modifier.fillMaxHeight()
                }
            )
    } else {
        Modifier.fillMaxSize()
    }
