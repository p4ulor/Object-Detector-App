package p4ulor.mediapipe.ui.screens

import android.Manifest
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult
import p4ulor.mediapipe.data.CameraConstants
import p4ulor.mediapipe.data.ObjectDetectorCallbacks
import p4ulor.mediapipe.data.ResultBundle
import p4ulor.mediapipe.data.viewmodel.MainViewModel
import p4ulor.mediapipe.e
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.CenteredContent
import p4ulor.mediapipe.ui.getActivityOrNull
import p4ulor.mediapipe.ui.onComposableDisposed
import p4ulor.mediapipe.ui.requestPermission

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    val isGranted = requestPermission(Manifest.permission.CAMERA, onPermissionNotGranted = {
        i("Permission not granted")
        CenteredContent {
            Text("No camera permission!")
            Button(onClick = {
                context.getActivityOrNull()?.requestPermission()
            }) {
                Text("Get permissions")
            }
        }
    })

    if(!isGranted) return

    i("Permission granted")

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    CameraX(viewModel, cameraProviderFuture)
}

/**
 * We're using CameraX to use the phone's camera, and since it doesn't have a prebuilt
 * composable in Jetpack Compose, we use AndroidView to implement it.
 * We are using CameraProvider and not CameraController for more customization
 * https://developer.android.com/media/camera/camerax/architecture#cameraprovider
 */
@Composable
fun CameraX(
    viewModel: MainViewModel,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var isCameraActive by remember { mutableStateOf(true) }

    onComposableDisposed {
        isCameraActive = false
        cameraProviderFuture.get().unbindAll()
    }

    // Contains the data necessary to outline an object into the screen
    var objectResults by remember {
        mutableStateOf<ObjectDetectorResult?>(null)
    }

    // In milliseconds
    var processingTime by rememberSaveable {
        mutableIntStateOf(0)
    }

    // CameraX isn't providing a composable yet, so we use AndroidView to use it
    AndroidView(
        factory = { ctx ->
            val cameraPreviewView = PreviewView(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Indicate the cameraProvider that we want to get the preview of the camera
                val previewUseCase = Preview.Builder().build().also {
                    it.setSurfaceProvider(cameraPreviewView.surfaceProvider)
                }

                viewModel.process(CameraConstants.imageAnalyzerUseCase, object : ObjectDetectorCallbacks {
                    override fun onResults(resultBundle: ResultBundle) {
                        if (isCameraActive) {
                            objectResults = resultBundle.result
                            processingTime = resultBundle.processingTime.toInt()
                        }
                    }

                    override fun onError(error: String) {
                        e("error")
                    }
                })

                // We close any currently open camera just in case, then open up
                // our own to be display the live camera feed
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraConstants.frontCamera,
                    previewUseCase,
                    CameraConstants.imageAnalyzerUseCase
                )
            }, ContextCompat.getMainExecutor(ctx))
            cameraPreviewView
        }
    )

    // Show the detected objects overlay
    objectResults?.let {
        ObjectBoundsBoxOverlay(
            results = it
        )
    }
}

@Composable
private fun ObjectBoundsBoxOverlay(
    results: ObjectDetectorResult
) {
    val detections = results.detections()
    if (detections != null) {
        for (detection in detections) {
            // calculating the UI dimensions of the detection bounds
            val resultBounds = detection.boundingBox()
            val boxWidth = resultBounds.width()
            val boxHeight = resultBounds.height()
            val boxLeftOffset = resultBounds.left
            val boxTopOffset = resultBounds.top

            Box(
                Modifier.fillMaxSize().offset(
                    boxLeftOffset.dp,
                    boxTopOffset.dp,
                )
            ) {
                Box(
                    modifier = Modifier
                        .border(3.dp, Color.Red)
                        .width(boxWidth.dp)
                        .height(boxHeight.dp)
                )
                Box(modifier = Modifier.padding(3.dp)) {
                    Text(
                        text = "${
                            detection.categories().first().categoryName()
                        } ${detection.categories().first().score().toString().take(4)}",
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(5.dp, 0.dp),
                        color = Color.White,
                    )
                }
            }
        }
    }
}
