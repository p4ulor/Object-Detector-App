package p4ulor.mediapipe.ui.screens.root

import android.content.res.Configuration
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import p4ulor.mediapipe.ui.components.utils.SmoothHorizontalDivider
import p4ulor.mediapipe.ui.components.utils.SystemNavigationBarHeight
import p4ulor.mediapipe.ui.components.utils.UiTestTag
import p4ulor.mediapipe.ui.theme.AppTheme

@Composable
fun BottomBar(currentScreen: Screen, onNavigateTo: (Screen) -> Unit){
    SmoothHorizontalDivider()
    NavigationBar(
        Modifier
            .height(SystemNavigationBarHeight + BottomNavigationBarHeight)
            .testTag(UiTestTag.bottomAppBar),
        containerColor = Color.Transparent
    ) {
        bottomBarDestinations.forEach { item ->
            BuildNavigationBarItem(item, currentScreen, onClick = { barItem ->
                if (currentScreen.name != barItem.screen.name) { // Avoids re-loading the route again
                    onNavigateTo(barItem.screen)
                }
            })
        }
    }
}

@Composable
private fun RowScope.BuildNavigationBarItem(
    item: NavItem,
    currentScreen: Screen,
    onClick: (item: NavItem) -> Unit,
) = NavigationBarItem(
    selected = currentScreen == item.screen,
    onClick = { onClick(item) },
    label = { Text(stringResource(item.screen.nameRes)) },
    alwaysShowLabel = false, // the label will only be shown when this item is selected (read docs)
    icon = {
        BadgedBox(
            badge = {
                if(item.hasNews) {
                    Badge()
                }
            }
        ) {
            Icon(
                painter = item.selectedIcon,
                contentDescription = item.screen.name,
                Modifier.size(item.size)
            )
        }
    }
)

/**
 * An abstraction over [Screen]. Specially to aid in the transformation of the icons.
 * The [Painter] type is used to support Image and Vector resources (.png and .xml) and ImageVectors
 */
private data class NavItem(
    val screen: Screen,
    val selectedIcon: Painter,
    val unselectedIcon: Painter? = null,
    val hasNews: Boolean,
    val size: Dp = 30.dp
) {
    override fun equals(other: Any?) = (other as? NavItem)?.screen == screen
}

/** Builds a list of [NavItem]s based on the [Screen] */
private val bottomBarDestinations
    @Composable
    get() = Screen.entries.map {
        NavItem(
            screen = it,
            selectedIcon = run {
                it.icon.asAppIcon?.resourcesIcon?.let { resourceIcon ->
                    painterResource(id = resourceIcon.resourceId)
                } ?: it.icon.asMaterialIcon?.materialIcon?.let { imageVector ->
                    rememberVectorPainter(image = imageVector)
                } ?: error("Something went wrong here")
            },
            hasNews = false,
            size = it.size
        )
    }

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_3
)
@Composable
private fun RootScreenPreview() = AppTheme {
    val currentScreen by rememberSaveable { mutableStateOf(Screen.Home) }
    BottomBar(currentScreen, onNavigateTo = {})
}
