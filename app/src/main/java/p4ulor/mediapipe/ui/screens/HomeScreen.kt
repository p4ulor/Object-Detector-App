package p4ulor.mediapipe.ui.screens

import android.Manifest
import android.content.ContextWrapper
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.common.util.concurrent.ListenableFuture
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.CenteredText
import p4ulor.mediapipe.ui.getActivityOrNull
import p4ulor.mediapipe.ui.onComposableDisposed
import p4ulor.mediapipe.ui.requestPermission
import p4ulor.mediapipe.ui.theme.MLonAndroidwMediaPipeTheme

@Composable
fun HomeScreen() {
    val isGranted = requestPermission(Manifest.permission.CAMERA, onPermissionNotGranted = {
        CenteredText("No camera permission!")
        i("Permission not granted")
    })

    if(!isGranted) return

    i("Permission granted")

    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var isCameraActive by remember { mutableStateOf(true) }

    onComposableDisposed {
        isCameraActive = false
        cameraProviderFuture.get().unbindAll()
    }

    Camera(cameraProviderFuture)
}

@Composable
fun Camera(cameraProviderFuture: ListenableFuture<ProcessCameraProvider>) {
    Box(
        Modifier.fillMaxSize()
    ) {
        CameraX(cameraProviderFuture)
    }
}

/**
 * We're using CameraX to use the phone's camera, and since it doesn't have a prebuilt
 * composable in Jetpack Compose, we use AndroidView to implement it
 * https://developer.android.com/media/camera/camerax/architecture
 */
@Composable
fun CameraX(cameraProviderFuture: ListenableFuture<ProcessCameraProvider>) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = { ctx ->
            // Simple case: using CameraController
            val cameraPreviewView = PreviewView(ctx)
            var cameraController = LifecycleCameraController(ctx)
            cameraController.bindToLifecycle(lifecycleOwner)
            cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraPreviewView.controller = cameraController

            cameraPreviewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    MLonAndroidwMediaPipeTheme {
        HomeScreen()
    }
}