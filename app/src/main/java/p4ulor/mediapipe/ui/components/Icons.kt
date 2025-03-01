package p4ulor.mediapipe.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
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

/** Todo, try to find a better solution, maybe */
data class AnyIcon private constructor(
    val appIcon: AppIcon? = null,
    val materialIcon: ImageVector? = null
){
    init {
        require(!(appIcon == null && materialIcon == null)) {
            "Both params can't be null"
        }
    }

    constructor(appIcon: AppIcon) : this(appIcon, null)
    constructor(materialIcon: ImageVector) : this(null, materialIcon)

    fun isAppIcon() = appIcon != null
}

/**
 * Drawable resources that are either .xml (can be created from an .svg, a vector image) or .png
 * To convert .svg to .xml, you can use [Android's Asset Studio](https://stackoverflow.com/a/56276118/28417805)
 * But it gave me errors, so I used https://svg2vector.com/ to convert .svg from
 * https://lucide.dev/icons/ to android .xml. All with stroke width 1.5px, size 300px (scaled up
 * by opening the .svg in Gimp and saving it as .png)
 * @param [resourceId] a .png or .xml
 */
enum class AppIcon(val resourceId: Int, val useOriginalColors: Boolean = false) {
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
fun QuickIcon(icon: AppIcon, onClick: () -> Unit) = Icon(
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

/** Useful when using [MaterialIcons], which are [ImageVector]s */
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
    icon: AnyIcon,
    onClick: () -> Unit,
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit = {_, _ -> }
){
    if(icon.isAppIcon()) {
        QuickIconWithBorder(icon.appIcon!!, onClick, onDrag)
    } else {
        QuickIconWithBorder(icon.materialIcon!!, onClick, onDrag)
    }
}

@Composable
private fun QuickIconWithBorder(
    icon: AppIcon,
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
    QuickIconWithBorder(AppIcon.Gemini, {})
}
