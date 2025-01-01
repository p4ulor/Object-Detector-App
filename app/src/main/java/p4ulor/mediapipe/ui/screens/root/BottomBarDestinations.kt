package p4ulor.mediapipe.ui.screens.root

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import p4ulor.mediapipe.ui.components.AppIcons

data class NavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector? = null,
    val hasNews: Boolean,
) {
    override fun equals(other: Any?) = (other as? NavItem)?.title == title
}

internal val bottomBarDestinations
    @Composable
    get() = listOf(
    NavItem(
        title = Screens.About.name,
        selectedIcon = Icons.Filled.Info,
        hasNews = false,
    ),
    NavItem(
        title = Screens.Home.name,
        selectedIcon = Icons.Filled.Home,
        hasNews = false,
    ),
    /*NavItem(
        title = "Settings",
        selectedIcon = ImageVector.vectorResource(id = AppIcons.Settings.resourceId),
        hasNews = true,
    )*/
)