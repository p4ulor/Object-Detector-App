package p4ulor.mediapipe.ui.screens.home

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import p4ulor.mediapipe.ui.components.AppIcons
import p4ulor.mediapipe.ui.components.Icon
import p4ulor.mediapipe.ui.components.IconDefaultSize
import p4ulor.mediapipe.ui.screens.root.BottomBarHeight
import kotlin.math.roundToInt

private val ExtraPadding = 50

/**
 * Everything regarding the button offset and it's dragging position but be in pixels (not dp)
 * or it doesn't work
 */
@Composable
fun FloatingMultiActionButton(){
    val context = LocalContext.current

    var isExpanded by remember { mutableStateOf(false) }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val display = context.resources.displayMetrics
    val density = LocalDensity.current
    val bottomBarHeight = with(density) { BottomBarHeight.toPx() }
    val iconSize = with(density) { IconDefaultSize.toPx() }
    val screenWidthPixels = display.widthPixels - iconSize - ExtraPadding
    val screenHeightPixels = display.heightPixels - bottomBarHeight - iconSize - ExtraPadding

    FloatingActionButton(
        onClick = {

        },
        modifier = Modifier.padding(bottom = 14.dp)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    val current = Offset(offsetX, offsetY)
                    val newPos = current + dragAmount
                    offsetX = newPos.x.coerceIn(0f, screenWidthPixels)
                    offsetY = newPos.y.coerceIn(0f, screenHeightPixels)
                }
            },
        containerColor = Color(0xB76D6D6D)
    ) {
        Icon(AppIcons.Camera){}
    }
}