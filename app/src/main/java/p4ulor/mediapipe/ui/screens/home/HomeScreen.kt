package p4ulor.mediapipe.ui.screens.home

import android.Manifest
import android.graphics.RectF
import android.view.View
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalZeroShutterLag
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.tasks.components.containers.Category
import com.google.mediapipe.tasks.components.containers.Detection
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.utils.CameraConstants
import p4ulor.mediapipe.android.utils.CameraConstants.toggle
import p4ulor.mediapipe.android.utils.getActivity
import p4ulor.mediapipe.android.viewmodels.MainViewModel
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.animations.HueShiftLooper
import p4ulor.mediapipe.ui.shapes.RoundRectangleShape
import p4ulor.mediapipe.ui.theme.rainbowWith
import p4ulor.mediapipe.ui.utils.CenteredContent
import p4ulor.mediapipe.android.utils.getSizeOfBoxKeepingRatioGivenContainer
import p4ulor.mediapipe.android.utils.imageAnalysisSettings
import p4ulor.mediapipe.android.utils.requestPermission
import p4ulor.mediapipe.android.utils.toInt
import p4ulor.mediapipe.android.utils.toSize
import p4ulor.mediapipe.data.domains.mediapipe.objectName

import java.util.concurrent.Executors
import kotlin.random.Random
import androidx.compose.ui.tooling.preview.Preview as PreviewComposable

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

    var cameraPreviewRatio by remember { mutableStateOf(CameraConstants.RATIO_16_9) }
    var camera by remember { mutableStateOf<Camera?>(null) }

    // Contains the data necessary to outline an object into the screen
    val resultsBundle by viewModel.results.collectAsState()

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
            val cameraProvider = cameraProviderFuture.get() // is throwable, but let's not overcomplicate
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val cameraPreviewView = PreviewView(ctx)
                    cameraProviderFuture.addListener(
                        {
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
                            cameraProvider.unbindAll()
                            camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                previewUseCase,
                                imageCaptureUseCase,
                                viewModel.imageAnalysisSettings
                            )

                        },
                        ContextCompat.getMainExecutor(ctx)
                    )

                    cameraPreviewView
                }
            )

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
                    val icon = if (isFlashEnabled) R.drawable.flashlight_off
                               else R.drawable.flashlight_on
                    Icon(
                        painterResource(icon),
                        contentDescription = "Flash",
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .size(64.dp)
                            .clickable {
                                isFlashEnabled = !isFlashEnabled
                                camera?.cameraControl?.enableTorch(isFlashEnabled)?.addListener( {
                                    i("Flashlight updated")
                                }, Executors.newSingleThreadExecutor())
                            })

                    Icon(
                        painterResource(R.drawable.aspect_ratio),
                        contentDescription = "Ratio",
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .size(64.dp)
                            .clickable {
                                cameraPreviewRatio = cameraPreviewRatio.toggle()
                            })
                }
            }
        } else {
            i("Torch not supportedD")
        }
    }
}

@Composable
private fun ObjectBoundsBoxOverlays(
    detections: List<Detection>,
    frameWidth: Int,
    frameHeight: Int,
    animate: Boolean = false
) {
    val borderWidth = 3.dp

    /**
     * Tracks positions of a single [Detection.objectName] in order to animate
     * transitions to new position and dimensions. TODO Allow storing various objects w/ same name
     */
    val currentBoundsForEachObject = remember { mutableMapOf<String, AnimatedDetectionOverlay>() }

    // Update animation states for all results
    if(animate){
        for (detection in detections) {
            val detectionBounds = detection.boundingBox()
            val animatableState = currentBoundsForEachObject.getOrPut(detection.objectName) {
                val detectionBounds = detection.boundingBox()
                AnimatedDetectionOverlay(
                    xLeft = Animatable(detectionBounds.left),
                    yTop = Animatable(detectionBounds.top),
                    width = Animatable(detectionBounds.width()),
                    height = Animatable(detectionBounds.height())
                )
            }

            // Update the bounds of the overlay progressively as defined by an animation for every new detectionBounds
            LaunchedEffect(detectionBounds) {
                animatableState.updateBoundingBox(detectionBounds)
            }
        }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        for (detection in detections) {
            // calculating the UI dimensions of the detection bounds based on the container
            // and based on the exact bounds,

            val scaler = OverlayScaler(
                frameWidth = frameWidth,
                frameHeight = frameHeight,
                containerWidth = this.maxWidth.value,
                containerHeight = this.maxHeight.value
            )
            val box = if(animate){
                val currBounds = currentBoundsForEachObject[detection.objectName] ?: continue
                scaler.scaleBox(currBounds)
            } else {
                scaler.scaleBox(detection.boundingBox())
            }

            // Text field with grey background with the name of the object
            Box(
                Modifier
                    .offset(box.xLeft.dp, box.yTop.dp)
                    .background(Color(0x4E4F4F4F), shape = RoundRectangleShape)) {
                val obj = detection.categories().first()
                Column {
                    Text(
                        text = "${obj.categoryName()} ${obj.score().toString().take(4)}",
                        modifier = Modifier
                            .width(box.width.dp)
                            .padding(borderWidth * 1.5f),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            val hueShiftLooper = remember { HueShiftLooper() }
            val hueShift by hueShiftLooper.hueShift.collectAsState()

            // Linear gradient Box border hue loop, goes back and fourth between the hue limits
            LaunchedEffect(Unit) {
                hueShiftLooper.start()
            }

            val rainBowBrush = Brush.linearGradient(
                colorStops = rainbowWith(hueShift),
                start = Offset(0f, 0f),
                end = Offset(box.width*8, box.height*8) // This helps in not showing too many colors at the same time, by stretching the gradient
            )

            // 2 sets of blurs for glow effect
            Box(
                Modifier
                    .offset(box.xLeft.dp, box.yTop.dp)
                    .width(box.width.dp)
                    .height(box.height.dp)
                    .blur(1.dp, BlurredEdgeTreatment.Unbounded)
                    .border(borderWidth, rainBowBrush, RoundRectangleShape)
            )

            Box(
                Modifier
                    .offset(box.xLeft.dp, box.yTop.dp)
                    .width(box.width.dp)
                    .height(box.height.dp)
                    .blur(16.dp, BlurredEdgeTreatment.Unbounded)
                    .border(borderWidth * 2, rainBowBrush, RoundRectangleShape)
            )
        }
    }
}


@PreviewComposable
@Composable
fun ObjectBoundsBoxOverlayPreview() {
    var cameraMovement by remember { mutableIntStateOf(0) }
    var score by remember { mutableFloatStateOf(0f) }
    val simulateMovement = false

    LaunchedEffect(Unit) {
        while (this.isActive) {
            if(simulateMovement){
                score = Random.nextFloat()
                cameraMovement = (Random.nextFloat()*40).toInt()
                delay(1000) // Update every 500ms
            }
        }
    }

    ObjectBoundsBoxOverlays(
        frameWidth = 720,
        frameHeight = 1280,
        detections = listOf(
            Detection.create(
                listOf(
                    Category.create(
                        score,
                        -1,
                        "Bottle aaaaaaa",
                        "" // always comes empty by default?
                    )
                ),
                with(cameraMovement){
                    RectF(
                        386.0f + this,
                        533.0f + this,
                        554.0f + this,
                        1052.0f + this
                    )
                }
            )
        )
    )
}
