package p4ulor.mediapipe.ui.screens.root

import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import p4ulor.mediapipe.ui.components.MaterialIcons

/** An abstraction over [Screens]. Specially to aid in the transformation of the icons */
data class NavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector? = null,
    val hasNews: Boolean,
    val size: Dp = 30.dp
) {
    override fun equals(other: Any?) = (other as? NavItem)?.title == title
}

/** Builds a list of [NavItem]s based on the [Screens] */
internal val bottomBarDestinations
    @Composable
    get() = Screens.entries.map {
        NavItem(
            title = it.name,
            selectedIcon = when {
                it.icon?.resourceVectorId!=null -> ImageVector.vectorResource(id = it.icon.resourceVectorId)
                it.materialIcons!=null -> it.materialIcons
                else -> error("Something wasn't setup properly here")
            },
            hasNews = false,
            size = it.size
        )
    }
