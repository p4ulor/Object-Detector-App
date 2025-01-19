package p4ulor.mediapipe.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.animations.slideInVertSmooth
import p4ulor.mediapipe.ui.animations.slideOutVertSmooth
import p4ulor.mediapipe.ui.animations.smooth
import p4ulor.mediapipe.ui.components.IconDefaultSize
import p4ulor.mediapipe.ui.screens.root.BottomBarHeight
import p4ulor.mediapipe.ui.theme.AppTheme
import kotlin.math.roundToInt

private const val ExtraPadding = 50

@Composable
fun ExpandableFAB(
    listOpenerFAB: FloatingActionButton,
    fabs: List<FloatingActionButton>,
    offset: IntOffset? = null
) = BoxWithConstraints(
    modifier = if(offset!=null) Modifier.offset { offset } else Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val context = LocalContext.current
    val display = context.resources.displayMetrics
    val density = LocalDensity.current
    val bottomBarHeight = with(density) { BottomBarHeight.toPx() }
    val iconSize = with(density) { IconDefaultSize.toPx() }
    val screenWidthPixels = display.widthPixels - iconSize - ExtraPadding
    val screenHeightPixels = display.heightPixels - bottomBarHeight - iconSize - ExtraPadding

    var _openUpwards by remember { mutableStateOf(true) }
    val openUpwards = if(isExpanded) {
        _openUpwards // dont change the button opening, if it's moved to a place that would change _openUpwards
    } else {
        _openUpwards = 3000 + offsetY + iconSize + fabs.size * iconSize >= screenHeightPixels
        _openUpwards
    }
    i("Button position = ${offsetX}, ${offsetY}, upwards=$openUpwards, ${offsetY + fabs.size * iconSize} >= $screenHeightPixels")

    val openerFab: @Composable () -> Unit = {
        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        val current = Offset(offsetX, offsetY)
                        val newPos = current + dragAmount
                        offsetX = newPos.x//.coerceIn(0f, screenWidthPixels)
                        offsetY = newPos.y//.coerceIn(0f, screenHeightPixels)
                    }
                },
        ) {
            Icon(imageVector = listOpenerFAB.icon, contentDescription = "listOpenerFAB")
        }
    }

    Box(Modifier.offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }){
        if(openUpwards){
            Column {
                ExpandableFabs(fabs, isVisible = isExpanded)
                openerFab()
            }
        } else {
            Column {
                openerFab()
                ExpandableFabs(fabs, isVisible = isExpanded)
            }
        }
    }
}

@Composable
private fun ExpandableFabs(fabs: List<FloatingActionButton>, isVisible: Boolean) {
    AnimatedVisibility(
        modifier = Modifier.zIndex(-1f), // So the expanded buttons dont show on top of the opener
        visible = isVisible,
        enter = fadeIn(smooth()) + slideInVertSmooth(),
        exit = fadeOut(smooth()) + slideOutVertSmooth()
    ) {
        LazyColumn {
            items(fabs){
                FloatingActionButton(
                    onClick = it.onClick,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(imageVector = it.icon, contentDescription = "fab")
                }
            }
        }
    }
}

data class FloatingActionButton(
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)

@Preview
@Composable
fun ExpandableFABPreview() = AppTheme {

    val context = LocalContext.current
    val display = context.resources.displayMetrics
    val density = LocalDensity.current
    val bottomBarHeight = with(density) { BottomBarHeight.toPx() }
    val iconSize = with(density) { IconDefaultSize.toPx() }
    val screenWidthPixels = display.widthPixels - iconSize - ExtraPadding
    val screenHeightPixels = display.heightPixels - bottomBarHeight - iconSize - ExtraPadding

    val putOnTop = false
    val yPosition = if(putOnTop) 0 else screenHeightPixels.toInt()

    Box(Modifier.fillMaxSize()) {
        ExpandableFAB(
            listOpenerFAB = FloatingActionButton(Icons.Default.Add),
            listOf(
                FloatingActionButton(Icons.Default.Edit) { i("Edit clicked") },
                FloatingActionButton(Icons.Default.Share) { i("Share clicked") }
            ),
            offset = IntOffset(x = 0, y = yPosition)
        )
    }
}