package p4ulor.mediapipe.ui.screens.home

import android.graphics.RectF
import androidx.compose.animation.core.Animatable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.mediapipe.tasks.components.containers.Category
import com.google.mediapipe.tasks.components.containers.Detection
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import p4ulor.mediapipe.data.domains.mediapipe.objectName
import p4ulor.mediapipe.ui.animations.HueShiftLooper
import p4ulor.mediapipe.ui.components.RoundRectangleShape
import p4ulor.mediapipe.ui.theme.rainbowWith
import kotlin.random.Random

@Composable
fun ObjectBoundsBoxOverlays(
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

@Preview
@Composable
private fun ObjectBoundsBoxOverlayPreview() {
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
