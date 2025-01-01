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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.utils.CameraConstants
import p4ulor.mediapipe.android.utils.CameraConstants.toggle
import p4ulor.mediapipe.android.utils.getActivity
import p4ulor.mediapipe.android.viewmodels.MainViewModel
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.components.CenteredContent
import p4ulor.mediapipe.android.utils.getSizeOfBoxKeepingRatioGivenContainer
import p4ulor.mediapipe.android.utils.createImageAnalyser
import p4ulor.mediapipe.android.utils.requestPermission
import p4ulor.mediapipe.android.utils.toInt
import p4ulor.mediapipe.android.utils.toSize
import p4ulor.mediapipe.ui.components.Icon
import p4ulor.mediapipe.ui.components.AppIcons
import java.util.concurrent.Executors

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    val isGranted = requestPermission(Manifest.permission.CAMERA, onPermissionNotGranted = {
        i("Permission not granted")
        CenteredContent {
            Text("No camera permission!")
            Button(onClick = {
                context.getActivity()?.requestPermission()
            }) {
                Text("Get permissions")
            }
        }
    })

    if(!isGranted) return
    i("Permission granted")
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    CameraPreviewContainer(viewModel, cameraProviderFuture)
}

/**
 * We're using CameraX to use the phone's camera, and since it doesn't have a prebuilt
 * composable in Jetpack Compose, we use AndroidView to implement it.
 * We are using CameraProvider and not CameraController for more customization
 * https://developer.android.com/media/camera/camerax/architecture#cameraprovider
 */
@OptIn(ExperimentalZeroShutterLag::class)
@Composable
fun CameraPreviewContainer(
    viewModel: MainViewModel,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var isAppMinimized by remember { mutableStateOf(false) }
    var cameraPreviewRatio by remember { mutableStateOf(CameraConstants.RATIO_16_9) }
    var camera by remember { mutableStateOf<Camera?>(null) } /** the camera we setup in [startCameraAndPreviewView] */
    val cameraProvider = remember { cameraProviderFuture.get() } // is throwable, but let's not overcomplicate

    // Contains the data necessary to outline an object into the screen
    val resultsBundle by viewModel.results.collectAsState()

    CameraUseBinder(
        lifecycleOwner,
        onBind = {
            i("Will re-bind camera")
            isAppMinimized = false
        },
        onUnbind = {
            i("Unbinding camera")
            cameraProvider.unbindAll()
            isAppMinimized = true
        }
    )

    BoxWithConstraints(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val cameraPreviewSize = getSizeOfBoxKeepingRatioGivenContainer(
            container = Size(width = this.maxWidth.value, height = this.maxHeight.value),
            box = with(cameraPreviewRatio.toSize()) {
                Size(width = width, height = height)
            }
        )

        Box(
            Modifier
                .width(cameraPreviewSize.width.dp)
                .height(cameraPreviewSize.height.dp),
        ) {
            // CameraX isn't providing a composable yet, so we use AndroidView to use it
            if(!isAppMinimized){ // The only way to terminate the PreviewView in order to avoid an occasional log spam updateSurface: surface is not valid when the app is minimized
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val cameraPreviewView = PreviewView(ctx)

                        val imageAnalysisSettings  =viewModel.initObjectDetector(createImageAnalyser(
                            ratioDeprecated = cameraPreviewRatio.toInt()
                        ))

                        camera = startCameraAndPreviewView(
                            cameraProvider,
                            cameraPreviewView,
                            imageAnalysisSettings,
                            cameraPreviewRatio,
                            lifecycleOwner
                        )

                        cameraPreviewView
                    }
                )
            }

            // Show the detected objects overlays
            resultsBundle?.let {
                ObjectBoundsBoxOverlays(
                    detections = it.result.detections() ?: emptyList(),
                    frameWidth = it.inputImageWidth,
                    frameHeight = it.inputImageHeight,
                    animate = viewModel.animateResults
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(Modifier.align(Alignment.BottomCenter)) {
            with(camera?.cameraInfo) {
                if (this?.hasFlashUnit() == true) {
                    i("Torch supported, state: ${torchState.value}")
                    var isFlashEnabled by remember { mutableStateOf(false) }
                    val icon = if (isFlashEnabled) AppIcons.FlashlightOff else AppIcons.FlashlightOn
                    Icon(icon) {
                        isFlashEnabled = !isFlashEnabled
                        camera?.cameraControl?.enableTorch(isFlashEnabled)?.addListener( {
                            i("Flashlight updated")
                        }, Executors.newSingleThreadExecutor())
                    }
                } else {
                    i("Torch not supported")
                }
            }

            Icon(AppIcons.Scale) {
                cameraPreviewRatio = cameraPreviewRatio.toggle()
            }
        }
    }
}

/**
 * Creates a camera [PreviewView] which is passed to the [cameraProvider] along with other
 * [UseCase]s (max 3), that are used with the camera
 * @return The created [Camera]
 */
fun startCameraAndPreviewView(
    cameraProvider: ProcessCameraProvider,
    cameraPreviewView: PreviewView,
    imageAnalysisSettings: ImageAnalysis,
    cameraPreviewRatio: ResolutionSelector,
    lifecycleOwner: LifecycleOwner
): Camera {

    // Indicate the cameraProvider that we want to get the preview of the camera
    val previewUseCase = Preview.Builder().build().also {
        it.setSurfaceProvider(cameraPreviewView.surfaceProvider)
    }

    // Not needed for flash, but can be used later
    val imageCaptureUseCase = ImageCapture.Builder()
        .build()

    // We close any currently open camera just in case, then open up
    // our own to display the live camera feed
    // cameraProvider.unbindAll()
    return cameraProvider.bindToLifecycle(
        lifecycleOwner,
        CameraSelector.DEFAULT_BACK_CAMERA,
        previewUseCase,
        imageCaptureUseCase,
        imageAnalysisSettings
    )
}
