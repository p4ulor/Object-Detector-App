package p4ulor.mediapipe.ui.screens.root

import android.content.res.Configuration
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.utils.create
import p4ulor.mediapipe.android.viewmodels.MainViewModel
import p4ulor.mediapipe.ui.components.AppIcons
import p4ulor.mediapipe.ui.components.MaterialIcons
import p4ulor.mediapipe.ui.components.utils.BoxWithBackground
import p4ulor.mediapipe.ui.components.utils.SmoothHorizontalDivider
import p4ulor.mediapipe.ui.screens.about.AboutScreen
import p4ulor.mediapipe.ui.screens.home.HomeScreen
import p4ulor.mediapipe.ui.screens.settings.SettingsScreen
import p4ulor.mediapipe.ui.theme.AppTheme

val BottomNavigationBarHeight = 65.dp

@Composable
fun RootScreen() = Surface { // The surface is used to for theming to work
    var currentScreenId by rememberSaveable { mutableStateOf(Screens.Home.name) }
    val navController = rememberNavController()

    BoxWithBackground(R.drawable.background_default) {

        // I'll keep this here for demo purposes, the other VM's are injected with Koin
        val viewModel = viewModel<MainViewModel>(
            factory = create(MainViewModel::class, LocalContext.current.applicationContext)
        )

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                SmoothHorizontalDivider()
                NavigationBar (Modifier.height(BottomNavigationBarHeight), containerColor = Color.Transparent) {
                    bottomBarDestinations.forEach { item ->
                        buildNavigationBarItem(item, currentScreenId, onClick = { barItem ->
                            if(currentScreenId != barItem.name){
                                currentScreenId = barItem.name
                                navController.navigate(item.name)
                            }
                        })
                    }
                }
            },
            content = {
                NavHost(
                    modifier = Modifier.padding(it), // Important so the screens automatically take in consideration the bottom bar
                    navController = navController,
                    startDestination = Screens.Home.name,
                ) {
                    composable(route = Screens.About.name) { AboutScreen() }
                    composable(route = Screens.Home.name) { HomeScreen(viewModel) }
                    composable(route = Screens.Settings.name) { SettingsScreen() }
                }
            }
        )
    }
}

@Composable
private fun RowScope.buildNavigationBarItem(
    item: NavItem,
    currentScreenId: String,
    onClick: (item: NavItem) -> Unit,
) = NavigationBarItem(
        selected = currentScreenId == item.name,
        onClick = { onClick(item) },
        label = { Text(item.name) },
        alwaysShowLabel = false, // the label will only be shown when this item is selected
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
                    contentDescription = item.name,
                    modifier = Modifier.size(item.size)
                )
            }
        }
    )

/**
 * This enum defines the order in which the destinations appear.
 * The names of the enums are also the id of the each screen
 */
enum class Screens(
    val icon: AppIcons? = null,
    val materialIcons: ImageVector? = null,
    val size: Dp = 25.dp
) {
    About(materialIcons = MaterialIcons.Info, size = 22.dp),
    Home(materialIcons = MaterialIcons.Home),
    Settings(icon = AppIcons.Settings)
}

/** If it's failing, comment out uses of [LocalContext.current]. Find a solution for this */
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun RootScreenPreview() = AppTheme {
    RootScreen()
}