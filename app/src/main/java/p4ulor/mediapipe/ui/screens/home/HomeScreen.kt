package p4ulor.mediapipe.ui.screens.home

import android.Manifest
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalZeroShutterLag
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.utils.CameraConstants
import p4ulor.mediapipe.android.utils.CameraConstants.toggle
import p4ulor.mediapipe.android.utils.createCameraImageAnalyser
import p4ulor.mediapipe.android.utils.createImageCaptureUseCase
import p4ulor.mediapipe.android.utils.enableFlash
import p4ulor.mediapipe.android.utils.getActivity
import p4ulor.mediapipe.android.utils.getCameraProvider
import p4ulor.mediapipe.android.utils.getSizeOfBoxKeepingRatioGivenContainer
import p4ulor.mediapipe.android.utils.hasFlash
import p4ulor.mediapipe.android.utils.is4by3
import p4ulor.mediapipe.android.utils.isHdrSupported
import p4ulor.mediapipe.android.utils.requestPermission
import p4ulor.mediapipe.android.utils.requestUserToManuallyAddThePermission
import p4ulor.mediapipe.android.utils.takePic
import p4ulor.mediapipe.android.utils.toInt
import p4ulor.mediapipe.android.utils.toSize
import p4ulor.mediapipe.android.viewmodels.HomeViewModel
import p4ulor.mediapipe.data.domains.mediapipe.Model
import p4ulor.mediapipe.data.domains.mediapipe.ObjectDetectorSettings
import p4ulor.mediapipe.data.storage.preferences.UserPreferences
import p4ulor.mediapipe.data.storage.preferences.UserSecretPreferences
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.animations.smooth
import p4ulor.mediapipe.ui.components.AnyIcon
import p4ulor.mediapipe.ui.components.AppIcon
import p4ulor.mediapipe.ui.components.ExpandableFAB
import p4ulor.mediapipe.ui.components.FloatingActionButton
import p4ulor.mediapipe.ui.components.MaterialIcons
import p4ulor.mediapipe.ui.components.QuickText
import p4ulor.mediapipe.ui.components.utils.CenteredContent
import p4ulor.mediapipe.ui.components.utils.DisplayHeight
import p4ulor.mediapipe.ui.components.utils.requestPermission
import p4ulor.mediapipe.ui.components.utils.toast
import p4ulor.mediapipe.ui.screens.home.overlay.ObjectBoundsBoxOverlays
import p4ulor.mediapipe.ui.screens.root.BottomNavigationBarHeight

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val ctx = LocalContext.current

    val isGranted = requestPermission(Manifest.permission.CAMERA, onPermissionNotGranted = {
        CenteredContent {
            /** See [requestPermission] */
            var oneTimePermRequestWasUsed by rememberSaveable { mutableStateOf(false) }
            QuickText(R.string.no_camera_permission)
            Button(onClick = {
                if(!oneTimePermRequestWasUsed){
                    ctx.getActivity()?.requestPermission()
                    oneTimePermRequestWasUsed = true
                } else {
                    ctx.requestUserToManuallyAddThePermission()
                }
            }) {
                QuickText(R.string.get_permissions)
            }
        }
    })

    if(isGranted){
        var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
        var prefs by remember { mutableStateOf<UserPreferences?>(null) }
        var secretPrefs by remember { mutableStateOf<UserSecretPreferences?>(null) }

        LaunchedEffect(Unit) {
            delay(500) // To let the initial launch animations to breathe
            prefs = viewModel.loadUserPrefs().first()
            secretPrefs = viewModel.loadUserSecretPrefs().first()
            cameraProvider = ctx.getCameraProvider()
        }

        if(cameraProvider!=null && prefs!=null && secretPrefs!=null) {
            CameraPreviewContainer(viewModel, cameraProvider!!, prefs!!, secretPrefs!!)
        } else {
            CenteredContent {
                CircularProgressIndicator(Modifier.size(100.dp))
            }
        }
    }
}

/**
 * I'm using CameraX to use the phone's camera, and since it doesn't have a prebuilt
 * composable in Jetpack Compose, we use AndroidView to implement it.
 * We are using CameraProvider and not CameraController for more customization
 * https://developer.android.com/media/camera/camerax/architecture#cameraprovider
 */
@OptIn(ExperimentalZeroShutterLag::class)
@Composable
fun CameraPreviewContainer(
    vm: HomeViewModel,
    cameraProvider: ProcessCameraProvider,
    prefs: UserPreferences,
    secretPrefs: UserSecretPreferences
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val ctx = LocalContext.current

    var camera by remember { mutableStateOf<Camera?>(null) } /** the camera we setup in [startCameraAndPreviewView] */
    var isFlashEnabled by rememberSaveable { mutableStateOf(false) }
    var cameraPreviewRatio by remember { mutableStateOf(CameraConstants.RATIO_16_9) }
    var imageCaptureUseCase by remember { mutableStateOf(createImageCaptureUseCase(cameraPreviewRatio)) }
    var isAppMinimized by rememberSaveable { mutableStateOf(false) }
    var isGeminiEnabled by rememberSaveable { mutableStateOf(false) }

    val resultsBundle by vm.objDetectionResults.collectAsState()
    val hasConnection by vm.network.hasConnection.collectAsState(initial = false)

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

    LaunchedEffect(hasConnection) {
        if(!hasConnection) isGeminiEnabled = false //todo, this should disable the chat not remove it if there are messages
    }

    if(!isAppMinimized) { // Avoids showing composables of this screen for some milliseconds when changing screens. And it's used terminate the PreviewView, in order to avoid an occasional log spam updateSurface: surface is not valid when the app is minimized. (Apparently this is the only way by indicating to not render the AndroidView in the compose tree)
        Box(Modifier.fillMaxSize()) {
            val (modifier, aligntment) = modifierAndAlignmentFor(cameraPreviewRatio)
            BoxWithConstraints(modifier, aligntment) {
                val cameraPreviewSize = getSizeOfBoxKeepingRatioGivenContainer( // Using with() so it's more readable
                    container = with(this@BoxWithConstraints) {
                        Size(width = maxWidth.value, height = maxHeight.value)
                    },
                    box = with(cameraPreviewRatio.toSize()) {
                        Size(width = width, height = height)
                    }
                )

                EdgeBars(cameraPreviewSize)

                Box(Modifier
                    .width(cameraPreviewSize.width.dp)
                    .height(cameraPreviewSize.height.dp)
                ) {
                    AndroidView( // CameraX isn't providing a composable yet for Camera Preview, so we use AndroidView to use it
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx -> // The following operations should be done in the main thread and are expensive, which can result in Choreographer complaining with "Skipped 42~ frames". Opening the camera with these usacases and using an AndroidView, may explain this, so maybe this can't be avoided
                            val cameraPreviewView = PreviewView(ctx)

                            val imageAnalysisSettings = vm.initObjectDetector(
                                createCameraImageAnalyser(cameraPreviewRatio.toInt()),
                                ObjectDetectorSettings(
                                    sensitivityThreshold = prefs.minDetectCertainty,
                                    maxObjectDetections = prefs.maxObjectDetections,
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

                    // Show the detected objects overlays
                    resultsBundle?.let {
                        ObjectBoundsBoxOverlays(
                            detections = it.detectedObjects.detections() ?: emptyList(),
                            frameWidth = it.inputImageWidth,
                            frameHeight = it.inputImageHeight,
                            animate = prefs.enableAnimations
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isGeminiEnabled,
                Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                enter = fadeIn(smooth()) + scaleIn()
            ) {

            }
        }

        ExpandableFAB(
            listOpenerFAB = FloatingActionButton(AnyIcon(MaterialIcons.Add)),
            fabs = buildList {
                add(
                    FloatingActionButton(AnyIcon(AppIcon.Camera)) {
                        imageCaptureUseCase.takePic(ctx) { outputFile, location ->
                            ctx.toast("Image saved in $location")
                        }
                    }
                )
                add(
                    FloatingActionButton(AnyIcon(AppIcon.Gemini)) {
                        if(hasConnection && secretPrefs.geminiApiKey.isNotBlank()){
                            isGeminiEnabled = !isGeminiEnabled
                        } else {
                            with(ctx){
                                toast(getString(R.string.check_internet_and_gemini_key))
                            }
                        }
                    }
                )
                add(
                    FloatingActionButton(AnyIcon(AppIcon.Scale)) {
                        cameraPreviewRatio = cameraPreviewRatio.toggle()
                        imageCaptureUseCase = createImageCaptureUseCase(cameraPreviewRatio)
                    }
                )

                camera?.apply {
                    if (hasFlash) {
                        i("Torch supported, state: ${cameraInfo.torchState.value}")
                        val icon = if (isFlashEnabled) AppIcon.FlashlightOff else AppIcon.FlashlightOn
                        add(
                            FloatingActionButton(AnyIcon(icon)) {
                                isFlashEnabled = !isFlashEnabled
                                enableFlash(isFlashEnabled)
                            }
                        )
                    } else {
                        i("Torch not supported")
                    }
                }
            }
        )
    }
}

/**
 * Creates a camera [PreviewView] which is passed to the [cameraProvider] along with other
 * [UseCase]s (max 3), that are used with the camera
 * @return The created [Camera]
 */
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

/**
 * Add bars in the sides so the background doesn't show, per example, when camera ratio == 4:3
 * if(isAppMinimized) is used to avoid displaying this background when changing screens
 */
@Composable
private fun EdgeBars(cameraPreviewSize: Size) {
    Box(Modifier
        .fillMaxWidth()
        .height(cameraPreviewSize.height.dp)
        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
    )
}

/**
 * Specifies the right modifer and alignment for 4_3 and 16_9
 * - 4_3 -> Top with height equal to half of the available height
 * - 16_9 -> Placed on bottom with max size
 */
@Composable
private fun modifierAndAlignmentFor(cameraPreviewRatio: ResolutionSelector) = Pair(
    if(cameraPreviewRatio.is4by3) {
        val maxAvailableHeightDp = DisplayHeight - BottomNavigationBarHeight
        Modifier.height(maxAvailableHeightDp / 2).fillMaxWidth()
    } else {
        Modifier.fillMaxSize()
    },
    if(cameraPreviewRatio.is4by3) {
        Alignment.TopCenter
    } else {
        Alignment.BottomCenter
    }
)