package p4ulor.obj.detector.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import p4ulor.obj.detector.i
import p4ulor.obj.detector.ui.animations.slideInVertSmooth
import p4ulor.obj.detector.ui.animations.slideOutVertSmooth
import p4ulor.obj.detector.ui.animations.smooth
import p4ulor.obj.detector.ui.screens.root.BottomNavigationBarHeight
import p4ulor.obj.detector.ui.theme.AppTheme

private val ExtraPadding = 10.dp
private val PaddingBetweenButtons = 2.dp

/** 
 * A Floating Action Button that opens up or down depending on the screen size and it's position.
 * and stays within the bounds of the screen. It also takes into consideration the
 * [IconDefaultSize], [BottomNavigationBarHeight] and [ExtraPadding]
 * Preferably, the FABs open downwards.
 * Everything regarding the button offset and it's dragging position must be in pixels (not dp)
 * or it doesn't work since detectDragGestures uses px
 */
@Composable
fun ExpandableFAB(
    listOpenerIcon: Icon,
    fabs: List<Fab>,
    initialPosition: FabPosition = FabPosition.TopRight
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val display = context.resources.displayMetrics
    val bottomBarHeight = with(density) { BottomNavigationBarHeight.toPx() }
    val iconContainerSizePx = with(density) { IconContainerDefaultSize.toPx() }
    val paddingBetweenButtonsPx = with(density) { PaddingBetweenButtons.toPx() }
    val extraPaddingPx = with(density) { ExtraPadding.toPx() }
    val maxAvailableWidthPx = display.widthPixels - iconContainerSizePx
    val maxAvailableHeightPx = display.heightPixels - bottomBarHeight - iconContainerSizePx

    val initialOffsetPx = if(initialPosition.isTopRight){
        IntOffset(x = (maxAvailableWidthPx - extraPaddingPx).toInt(), y = extraPaddingPx.toInt())
    } else {
        IntOffset(x = (maxAvailableWidthPx - extraPaddingPx).toInt(), y = maxAvailableHeightPx.toInt())
    }

    Box(Modifier.fillMaxSize()) { // Cover the max area available
        var isExpanded by remember { mutableStateOf(false) }
        var openerFabOffsetX by remember { mutableFloatStateOf(initialOffsetPx.x.toFloat()) }
        var openerFabOffsetY by remember { mutableFloatStateOf(initialOffsetPx.y.toFloat()) }
        var canOpenUpwards by remember { mutableStateOf(initialPosition.isBottomRight) }
        var fabsYoffset by remember { mutableIntStateOf((iconContainerSizePx + paddingBetweenButtonsPx).toInt()) }

        LaunchedEffect(openerFabOffsetY, isExpanded, fabs.hashCode()) {
            canOpenUpwards = run {
                val maxYreachOfTheFabs = openerFabOffsetY + iconContainerSizePx + fabs.size * (iconContainerSizePx + paddingBetweenButtonsPx)
                if(isExpanded) {
                    canOpenUpwards
                } else {
                    maxYreachOfTheFabs >= maxAvailableHeightPx
                }
            }

            // Used to place the fabs either bellow or above the openerFab. Using columns causes the openerFab to change abruptly change position
            fabsYoffset = if (isExpanded) { // Logic to make the opening/closing of the fabs be done correctly when moving it through the screen through various states
                run {
                    if (canOpenUpwards) {
                        (-fabs.size * iconContainerSizePx) - paddingBetweenButtonsPx*2
                    } else {
                        iconContainerSizePx + paddingBetweenButtonsPx
                    }
                }.toInt()
            } else {
                fabsYoffset
            }
        }

        // FAB opener + fabs
        Box(Modifier.offset { IntOffset(openerFabOffsetX.toInt(), openerFabOffsetY.toInt()) }) {
            EzIconWithBorder(
                listOpenerIcon,
                onClick = { isExpanded = !isExpanded },
                onDrag = { change, dragAmount ->
                    val current = Offset(openerFabOffsetX, openerFabOffsetY)
                    val newPos = current + dragAmount
                    openerFabOffsetX = newPos.x.coerceIn(0f, maxAvailableWidthPx)
                    openerFabOffsetY = newPos.y.coerceIn(0f, maxAvailableHeightPx)
                }
            )

            // The FABs
            Box(Modifier
                .offset { IntOffset(x = 0, y = fabsYoffset) }
                .zIndex(-1f) // So the fabs are placed under the openerFAB
            ) {
                ExpandableFabs(fabs, canOpenUpwards, isVisible = isExpanded)
            }
        }
    }
}

@Composable
private fun ExpandableFabs(fabs: List<Fab>, openUpwards: Boolean, isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        Modifier.clipToBounds(), // Prevents the first or more FABs to show up slightly under and beyond the openerFAB's area when there are many FABs, due to the initial position of the slideIn animation
        enter = fadeIn(smooth()) + slideInVertSmooth(openUpwards),
        exit = fadeOut(smooth()) + slideOutVertSmooth(!openUpwards)
    ) {
        LazyColumn (verticalArrangement = Arrangement.spacedBy(PaddingBetweenButtons)) {
            items(fabs){
                EzIconWithBorder(it.icon, it.onClick)
            }
        }
    }
}

data class Fab(
    val icon: Icon,
    val onClick: () -> Unit = {}
)

enum class FabPosition {
    TopRight,
    BottomRight;

    val isTopRight: Boolean
        get() = this == TopRight

    val isBottomRight: Boolean
        get() = this == BottomRight
}

@Preview
@Composable
private fun ExpandableFABPreview() = AppTheme {
    ExpandableFAB(
        listOpenerIcon = Icon.Material(MaterialIcons.Add),
        listOf(
            Fab(Icon.App(ResourcesIcon.Camera)) { i("Edit clicked") },
            Fab(Icon.App(ResourcesIcon.Gemini)) { i("Share clicked") },
            Fab(Icon.App(ResourcesIcon.Camera)) { i("Edit clicked") },
            Fab(Icon.App(ResourcesIcon.Gemini)) { i("Share clicked") }
        ),
        initialPosition = FabPosition.TopRight
    )
}
