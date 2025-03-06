package p4ulor.mediapipe.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import p4ulor.mediapipe.R
import p4ulor.mediapipe.ui.theme.PreviewComposable
import androidx.compose.material.icons.Icons as ComposeMaterialIcons

sealed class Icon private constructor() {
    data class App(val resourcesIcon: ResourcesIcon) : Icon()
    data class Material(val materialIcon: ImageVector) : Icon()

    val asAppIcon get() = this as? App
    val asMaterialIcon get() = this as? Material
}

/**
 * Drawable resources that are either .xml (can be created from an .svg, a vector image) or .png
 * To convert .svg to .xml, you can use [Android's Asset Studio](https://stackoverflow.com/a/56276118/28417805)
 * But it gave me errors, so I used https://svg2vector.com/ to convert .svg from
 * https://lucide.dev/icons/ to android .xml. All with stroke width 1.5px, size 300px (scaled up
 * by opening the .svg in Gimp and saving it as .png)
 * @param [resourceId] a .png or .xml
 */
enum class ResourcesIcon(val resourceId: Int, val useOriginalColors: Boolean = false) {
    FlashlightOff(R.drawable.flashlight_off),
    FlashlightOn(R.drawable.flashlight_on),
    Scale(R.drawable.scale),
    Settings(R.drawable.settings_vector),
    Camera(R.drawable.camera),
    MediaPipe(R.drawable.mediapipe, useOriginalColors = true),
    Gemini(R.drawable.gemini, useOriginalColors = true);
}

/** Note: icons from [androidx.compose.material.icons.Icons] are [ImageVector]s */
val MaterialIcons = ComposeMaterialIcons.Default
/** To handle deprecated icons with the introduction of AutoMirrored */
val MaterialIconsExt = ComposeMaterialIcons.AutoMirrored.Default

val PaddingAroundIcon = 10.dp
val IconDefaultSize = 44.dp
val IconMediumSize = 30.dp
val IconSmallSize = 25.dp

@Composable
fun QuickIcon(icon: ResourcesIcon, onClick: () -> Unit) = Icon(
    painter = painterResource(icon.resourceId),
    contentDescription = icon.name,
    Modifier
        .size(IconDefaultSize)
        .padding(PaddingAroundIcon)
        .clickable {
            onClick()
        },
    tint = Color.White // MaterialTheme.colorScheme.onBackground
)

/** Useful when using [MaterialIcons], which are [ImageVector]s. An alternative to [IconButton] */
@Composable
fun QuickIcon(icon: ImageVector, size: Dp? = null, onClick: () -> Unit) = Icon(
    imageVector = icon,
    contentDescription = icon.name,
    Modifier
        .padding(PaddingAroundIcon)
        .size(size ?: IconDefaultSize)
        .clickable {
            onClick()
        },
    tint = Color.White
)

val IconInContainerDefaultSize = 40.dp
val IconContainerDefaultSize = 55.dp

@Composable
fun QuickIconWithBorder(
    icon: Icon,
    onClick: () -> Unit,
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit = {_, _ -> }
){
    icon.asAppIcon?.let {
        QuickIconWithBorder(it.resourcesIcon, onClick, onDrag)
    } ?: icon.asMaterialIcon?.let {
        QuickIconWithBorder(it.materialIcon, onClick, onDrag)
    }
}

@Composable
private fun QuickIconWithBorder(
    icon: ResourcesIcon,
    onClick: () -> Unit,
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit = {_, _ -> }
){
    FloatingActionButton(
        onClick = onClick,
        Modifier
            .size(IconContainerDefaultSize)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount -> onDrag(change, dragAmount) }
            }
    ) {
        Icon(
            painter = painterResource(icon.resourceId),
            icon.name,
            Modifier.size(IconInContainerDefaultSize),
            tint = if (icon.useOriginalColors) {
                Color.Unspecified
            } else {
                MaterialTheme.colorScheme.onPrimaryContainer
            }
        )
    }
}

@Composable
private fun QuickIconWithBorder(
    icon: ImageVector,
    onClick: () -> Unit,
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit = {_, _ -> }
){
    FloatingActionButton(
        onClick = onClick,
        Modifier
            .size(IconContainerDefaultSize)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount -> onDrag(change, dragAmount) }
            }
    ) {
        Icon(
            imageVector = icon,
            icon.name,
            Modifier.size(IconInContainerDefaultSize),
            tint = Color.White // MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview
@Composable
private fun QuickIconWithBorderPreview() = PreviewComposable {
    QuickIconWithBorder(ResourcesIcon.Gemini, {})
}
