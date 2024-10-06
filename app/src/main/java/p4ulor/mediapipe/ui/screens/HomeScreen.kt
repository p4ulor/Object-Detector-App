package p4ulor.mediapipe.ui.screens

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.CenteredContent
import p4ulor.mediapipe.ui.getActivityOrNull
import p4ulor.mediapipe.ui.onComposableDisposed
import p4ulor.mediapipe.ui.requestPermission

@Composable
fun HomeScreen() {
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
    var isCameraActive by remember { mutableStateOf(true) }

    onComposableDisposed {
        isCameraActive = false
        cameraProviderFuture.get().unbindAll()
    }

    CameraX(cameraProviderFuture)
}

/**
 * We're using CameraX to use the phone's camera, and since it doesn't have a prebuilt
 * composable in Jetpack Compose, we use AndroidView to implement it.
 * We are using CameraProvider and not CameraController for more customization
 * https://developer.android.com/media/camera/camerax/architecture#cameraprovider
 */
@Composable
fun CameraX(cameraProviderFuture: ListenableFuture<ProcessCameraProvider>) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val cameraPreviewView = PreviewView(ctx)
            
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Specify to use back camera
                val frontCamera = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                val imageAnalyzerUseCase = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .build()

                val previewUseCase = Preview.Builder().build().also {
                    it.setSurfaceProvider(cameraPreviewView.surfaceProvider)
                }

                // We close any currently open camera just in case, then open up
                // our own to be display the live camera feed
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    frontCamera,
                    imageAnalyzerUseCase,
                    previewUseCase
                )
            }, ContextCompat.getMainExecutor(ctx))
            cameraPreviewView
        },
        modifier = Modifier.fillMaxSize()
    )
}
