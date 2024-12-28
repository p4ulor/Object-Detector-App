package p4ulor.mediapipe.ui.screens

import android.Manifest
import android.graphics.RectF
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.tasks.components.containers.Category
import com.google.mediapipe.tasks.components.containers.Detection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import p4ulor.mediapipe.data.utils.CameraConstants
import p4ulor.mediapipe.data.utils.imageAnalysisSettings
import p4ulor.mediapipe.data.utils.objectName
import p4ulor.mediapipe.data.utils.toInt
import p4ulor.mediapipe.data.utils.toSize
import p4ulor.mediapipe.data.viewmodel.MainViewModel
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.animations.HueShiftLooper
import p4ulor.mediapipe.ui.animations.smooth
import p4ulor.mediapipe.ui.utils.CenteredContent
import p4ulor.mediapipe.ui.utils.getActivity
import p4ulor.mediapipe.ui.utils.requestPermission
import p4ulor.mediapipe.ui.shapes.RoundRectangleShape
import p4ulor.mediapipe.ui.theme.rainbowWith
import p4ulor.mediapipe.ui.utils.getSizeOfBoxKeepingRatioGivenContainer
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
@Composable
fun CameraPreviewContainer(
    viewModel: MainViewModel,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
) {
    var isCameraActive by remember { mutableStateOf(true) }
    var cameraPreviewRatio by remember { mutableStateOf(CameraConstants.RATIO_16_9) }

    val lifecycleOwner = LocalLifecycleOwner.current

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

        Box(Modifier
            .width(cameraPreviewSize.width.dp)
            .height(cameraPreviewSize.height.dp),
        ) {
            // CameraX isn't providing a composable yet, so we use AndroidView to use it
            val cameraProvider = cameraProviderFuture.get() ?: return@Box

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

                            viewModel.initObjectDetector(imageAnalysisSettings(
                                ratioDeprecated = cameraPreviewRatio.toInt()
                            ))

                            // We close any currently open camera just in case, then open up
                            // our own to display the live camera feed
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraConstants.frontCamera,
                                previewUseCase,
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
     * transitions to new position and dimensions
     */
    val currentBoundsForEachObject = remember { mutableMapOf<String, DetectionAnimationState>() }

    // Update animation states for all results

    if(animate){
        for (detection in detections) {
            val detectionBounds = detection.boundingBox()
            val animatableState = currentBoundsForEachObject.getOrPut(detection.objectName) {
                val detectionBounds = detection.boundingBox()
                DetectionAnimationState(
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
            val currBounds = currentBoundsForEachObject[detection.objectName] ?: continue
            val boxWidth = (currBounds.width.value / frameWidth) * this.maxWidth.value
            val boxHeight = (currBounds.height.value / frameHeight) * this.maxHeight.value
            val boxLeftOffset = (currBounds.xLeft.value / frameWidth) * this.maxWidth.value
            val boxTopOffset = (currBounds.yTop.value / frameHeight) * this.maxHeight.value

            // Text field with grey background
            Box(Modifier.offset(boxLeftOffset.dp, boxTopOffset.dp)
                .background(Color(0x4E4F4F4F), shape = RoundRectangleShape)) {
                val obj = detection.categories().first()
                Column {
                    Text(
                        text = "${obj.categoryName()} ${obj.score().toString().take(4)}",
                        modifier = Modifier.width(boxWidth.dp).padding(borderWidth*1.5f),
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
                end = Offset(boxWidth*8, boxHeight*8) // This helps in not showing too many colors at the same time, by stretching the gradient
            )

            // 2 sets of blurs for glow effect
            Box(Modifier.offset(boxLeftOffset.dp, boxTopOffset.dp)
                .width(boxWidth.dp).height(boxHeight.dp).blur(1.dp , BlurredEdgeTreatment.Unbounded)
                .border(borderWidth, rainBowBrush, RoundRectangleShape)
            )

            Box(Modifier.offset(boxLeftOffset.dp, boxTopOffset.dp)
                .width(boxWidth.dp).height(boxHeight.dp).blur(16.dp , BlurredEdgeTreatment.Unbounded)
                .border(borderWidth*2, rainBowBrush, RoundRectangleShape)
            )
        }
    }
}

/** Utility class used for animating the bounds of a detection, by using the 4 corners of the box */
private class DetectionAnimationState(
    val xLeft: Animatable<Float, AnimationVector1D>,
    val yTop: Animatable<Float, AnimationVector1D>,
    val width: Animatable<Float, AnimationVector1D>,
    val height: Animatable<Float, AnimationVector1D>
) {
    /**
     * 4 coroutines are required so the 4 values are updated in parallel, since [animateTo] is a
     * suspend func. Otherwise, it will be visible how each value (and dimension) updates in steps
     */
    suspend fun updateBoundingBox(newBox: RectF) = withContext(Dispatchers.Default) {
        launch { xLeft.animateTo(newBox.left, smooth()) }
        launch { yTop.animateTo(newBox.top, smooth()) }
        launch { width.animateTo(newBox.width(), smooth()) }
        launch { height.animateTo(newBox.height(), smooth()) }
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
