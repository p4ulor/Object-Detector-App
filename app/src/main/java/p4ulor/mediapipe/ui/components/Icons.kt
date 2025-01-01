package p4ulor.mediapipe.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons as ComposeMaterialIcons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import p4ulor.mediapipe.R
import p4ulor.mediapipe.ui.theme.ColorInvertFilter

enum class AppIcons(val resourceId: Int) {
    FlashlightOff(R.drawable.flashlight_off),
    FlashlightOn(R.drawable.flashlight_on),
    Scale(R.drawable.scale),
    Settings(R.drawable.settings)
}

/** Note: icons from [androidx.compose.material.icons.Icons] are [ImageVector]s */
val MaterialIcons = ComposeMaterialIcons.Default

@Composable
fun Icon(icon: AppIcons, onClick: () -> Unit) {
    Icon(painterResource(icon.resourceId),
        contentDescription = icon.name,
        modifier = Modifier
            .padding(bottom = 24.dp)
            .size(64.dp)
            .clickable {
                onClick()
            },
        //colorFilter = ColorInvertFilter
    )
}