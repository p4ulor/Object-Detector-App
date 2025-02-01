package p4ulor.mediapipe.ui.screens.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * An abstraction over [Screens]. Specially to aid in the transformation of the icons. [Painter] is
 * used to support Image and Vector resources and ImageVectors
 * @param [name] is also the identifier of the screen
 */
data class NavItem(
    val name: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter? = null,
    val hasNews: Boolean,
    val size: Dp = 30.dp
) {
    override fun equals(other: Any?) = (other as? NavItem)?.name == name
}

/** Builds a list of [NavItem]s based on the [Screens] */
val bottomBarDestinations
    @Composable
    get() = Screens.entries.map {
        NavItem(
            name = it.name,
            selectedIcon = when {
                it.icon?.resourceId!=null -> painterResource(id = it.icon.resourceId)
                it.materialIcons!=null -> rememberVectorPainter(it.materialIcons)
                else -> error("Something wasn't setup properly here")
            },
            hasNews = false,
            size = it.size
        )
    }
