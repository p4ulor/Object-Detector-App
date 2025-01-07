package p4ulor.mediapipe.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons as ComposeMaterialIcons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import p4ulor.mediapipe.R

/**
 * Drawable resources that are either .xml (can be created from an .svg, a vector image) or .png
 * To convert .svg to .xml, you can use [Android's Asset Studio](https://stackoverflow.com/a/56276118/28417805)
 * But it gave me errors, so I used https://svg2vector.com/ to convert .svg from
 * https://lucide.dev/icons/ to android .xml. All with stroke width 1.5px, size 300px (scaled up
 * by opening the .svg in Gimp)
 */
enum class AppIcons(val resourceId: Int, val resourceVectorId: Int? = null) {
    FlashlightOff(R.drawable.flashlight_off),
    FlashlightOn(R.drawable.flashlight_on),
    Scale(R.drawable.scale),
    Settings(R.drawable.settings_vector, R.drawable.settings_vector),
    Camera(R.drawable.camera)
}

/** Note: icons from [androidx.compose.material.icons.Icons] are [ImageVector]s */
val MaterialIcons = ComposeMaterialIcons.Default

val IconDefaultSize = 44.dp

@Composable
fun Icon(icon: AppIcons, onClick: () -> Unit) = Icon(
    painter = painterResource(icon.resourceId),
    contentDescription = icon.name,
    modifier = Modifier
        .size(IconDefaultSize)
        .clickable {
            onClick()
        },
    tint = Color.White // MaterialTheme.colorScheme.onBackground
)


/** Useful when using [MaterialIcons], which are [ImageVector]s */
@Composable
fun Icon(icon: ImageVector, onClick: () -> Unit) = Icon(
    imageVector = icon,
    contentDescription = icon.name,
    modifier = Modifier
        .padding(bottom = 24.dp)
        .size(IconDefaultSize)
        .clickable {
            onClick()
        },
    tint = Color.White
)
