package p4ulor.mediapipe.ui.screens.home

import android.Manifest
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalZeroShutterLag
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.utils.CameraConstants
import p4ulor.mediapipe.android.utils.CameraConstants.toggle
import p4ulor.mediapipe.android.utils.getActivity
import p4ulor.mediapipe.android.viewmodels.MainViewModel
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.utils.CenteredContent
import p4ulor.mediapipe.android.utils.getSizeOfBoxKeepingRatioGivenContainer
import p4ulor.mediapipe.android.utils.imageAnalysisSettings
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
    var camera by remember { mutableStateOf<Camera?>(null) }
    val cameraProvider = remember { cameraProviderFuture.get() } // is throwable, but let's not overcomplicate

    // Contains the data necessary to outline an object into the screen
    val resultsBundle by viewModel.results.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            if(event == Lifecycle.Event.ON_PAUSE){
                i("Unbinding camera")
                cameraProvider.unbindAll()
                isAppMinimized = true
            }
            if(event == Lifecycle.Event.ON_RESUME){
                i("Will re-bind camera")
                isAppMinimized = false
            }
        }

        lifecycle.addObserver(observer)
        onDispose {
            i("onDispose")
            lifecycle.removeObserver(observer)
        }
    }

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
            if(!isAppMinimized){
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val cameraPreviewView = PreviewView(ctx)
                        camera = startCameraAndPreviewView(
                            cameraProvider,
                            cameraPreviewView,
                            viewModel,
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

    with(camera?.cameraInfo){
        if (this?.hasFlashUnit() == true) {
            i("Torch supported, state: ${torchState.value}")
            Box(modifier = Modifier.fillMaxSize()) {
                var isFlashEnabled by remember { mutableStateOf(false) }
                Row(Modifier.align(Alignment.BottomCenter)) {
                    val icon = if (isFlashEnabled) AppIcons.FlashlightOff else AppIcons.FlashlightOn
                    Icon(icon) {
                        isFlashEnabled = !isFlashEnabled
                        camera?.cameraControl?.enableTorch(isFlashEnabled)?.addListener( {
                            i("Flashlight updated")
                        }, Executors.newSingleThreadExecutor())
                    }
                    Icon(AppIcons.Scale) {
                        cameraPreviewRatio = cameraPreviewRatio.toggle()
                    }
                }
            }
        } else {
            i("Torch not supportedD")
        }
    }
}

fun startCameraAndPreviewView(
    cameraProvider: ProcessCameraProvider,
    cameraPreviewView: PreviewView,
    viewModel: MainViewModel,
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

    viewModel.initObjectDetector(imageAnalysisSettings(
        ratioDeprecated = cameraPreviewRatio.toInt()
    ))

    // We close any currently open camera just in case, then open up
    // our own to display the live camera feed
    // cameraProvider.unbindAll()
    return cameraProvider.bindToLifecycle(
        lifecycleOwner,
        CameraSelector.DEFAULT_BACK_CAMERA,
        previewUseCase,
        imageCaptureUseCase,
        viewModel.imageAnalysisSettings
    )
}
