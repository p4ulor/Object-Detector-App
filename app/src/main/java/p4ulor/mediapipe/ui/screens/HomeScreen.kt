package p4ulor.mediapipe.ui.screens

import android.Manifest
import android.graphics.RectF
import android.view.View
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import p4ulor.mediapipe.data.utils.CameraConstants
import p4ulor.mediapipe.data.utils.imageAnalysisSettings
import p4ulor.mediapipe.data.utils.toInt
import p4ulor.mediapipe.data.utils.toSize
import p4ulor.mediapipe.data.viewmodel.MainViewModel
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.utils.CenteredContent
import p4ulor.mediapipe.ui.utils.getActivityOrNull
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
                context.getActivityOrNull()?.requestPermission()
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
    var cameraPreviewRatio by remember { mutableStateOf(CameraConstants.RATIO_4_3) }

    //CameraPreview(viewModel, cameraProviderFuture)
    val lifecycleOwner = LocalLifecycleOwner.current

    // Contains the data necessary to outline an object into the screen
    val resultsBundle by viewModel.results.collectAsState()

    BoxWithConstraints(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val cameraPreviewSize = getSizeOfBoxKeepingRatioGivenContainer(
            container = Size(
                width = this.maxWidth.value,
                height = this.maxHeight.value,
            ),
            box = with(cameraPreviewRatio.toSize()) {
                Size(
                    width = resultsBundle?.inputImageWidth?.toFloat() ?: width,
                    height = resultsBundle?.inputImageHeight?.toFloat() ?: height
                )
            }
        )
        Box(Modifier
            .width(cameraPreviewSize.width.dp)
            .height(cameraPreviewSize.height.dp),
            contentAlignment = Alignment.Center
        ) {
            // CameraX isn't providing a composable yet, so we use AndroidView to use it
            val cameraProvider = cameraProviderFuture.get() ?: return@Box

            AndroidView(
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
                ObjectBoundsBoxOverlay(
                    detections = it.result.detections() ?: emptyList(),
                    frameWidth = it.inputImageWidth,
                    frameHeight = it.inputImageHeight
                )
            }
        }
    }
}

@Composable
private fun ObjectBoundsBoxOverlay(
    detections: List<Detection>,
    frameWidth: Int,
    frameHeight: Int,
) {
    val borderWidth = 3.dp
    for (detection in detections) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            // calculating the UI dimensions of the detection bounds
            val resultBounds = detection.boundingBox()
            val boxWidth = (resultBounds.width() / frameWidth) * this.maxWidth.value
            val boxHeight = (resultBounds.height() / frameHeight) * this.maxHeight.value
            val boxLeftOffset = (resultBounds.left / frameWidth) * this.maxWidth.value
            val boxTopOffset = (resultBounds.top / frameHeight) * this.maxHeight.value

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

            var hueShift by remember { mutableFloatStateOf(0f) }
            var isHueShiftIncrement by remember { mutableStateOf(true) }

            // Linear gradient Box border hue loop, goes back and fourth between the hue limits
            LaunchedEffect(Unit) {
                while (this.isActive) {
                    // Gradually shift the colors in the gradient
                    hueShift += if (isHueShiftIncrement) 3f else -3f
                    if(hueShift>360f) {
                        hueShift = 360f
                        isHueShiftIncrement = false
                    }
                    if(hueShift<0f) {
                        hueShift = 0f
                        isHueShiftIncrement = true
                    }
                    delay(50)
                }
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

@PreviewComposable
@Composable
fun ObjectBoundsBoxOverlayPreview() {
    var cameraMovement by remember { mutableIntStateOf(0) }
    var score by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (this.isActive) {
            return@LaunchedEffect
            score = Random.nextFloat()
            cameraMovement = (Random.nextFloat()*40).toInt()
            delay(200) // Update every 500ms
        }
    }

    ObjectBoundsBoxOverlay(
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
