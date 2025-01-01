package p4ulor.mediapipe.ui.screens.root

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import p4ulor.mediapipe.android.viewmodels.MainViewModel
import p4ulor.mediapipe.android.utils.MainViewModelFactory
import p4ulor.mediapipe.ui.components.AppIcons
import p4ulor.mediapipe.ui.screens.home.HomeScreen
import p4ulor.mediapipe.ui.theme.AppTheme
import p4ulor.mediapipe.ui.components.MaterialIcons
import p4ulor.mediapipe.ui.screens.about.AboutScreen
import p4ulor.mediapipe.ui.screens.settings.SettingsScreen

@Composable
fun RootScreen(){
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {

            var currentDestination by remember { mutableStateOf<NavItem?>(null) }
            val navController = rememberNavController()

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar (Modifier.height(65.dp)) {
                        bottomBarDestinations.forEach { item ->
                            buildNavigationBarItem(item, currentDestination, this, onClick = {
                                currentDestination = it
                                navController.navigate(item.title)
                            })
                        }
                    }
                },
                content = { paddingPadding ->

                    val viewModel: MainViewModel = viewModel(
                        factory = MainViewModelFactory(LocalContext.current)
                    )

                    NavHost(
                        modifier = Modifier.padding(paddingPadding),
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
}

@Composable
private fun buildNavigationBarItem(
    item: NavItem,
    currentDestination: NavItem?,
    rowScope: RowScope,
    onClick: (item: NavItem) -> Unit,
) = with(rowScope) {
    NavigationBarItem(
        selected = currentDestination == item,
        onClick = { onClick(item) },
        label = { Text(item.title) },
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
                    imageVector = item.selectedIcon,
                    contentDescription = item.title,
                    modifier = Modifier.size(item.size)
                )
            }
        }
    )
}

/** This enum defines the order in which the destinations appear */
enum class Screens(
    val icon: AppIcons? = null,
    val materialIcons: ImageVector? = null,
    val size: Dp = 30.dp
) {
    About(materialIcons = MaterialIcons.Info, size = 25.dp),
    Home(materialIcons = MaterialIcons.Home),
    Settings(icon = AppIcons.Settings, size = 28.dp)
}