package p4ulor.mediapipe.ui.screens.root

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.utils.create
import p4ulor.mediapipe.android.utils.getActivity
import p4ulor.mediapipe.android.viewmodels.MainViewModel
import p4ulor.mediapipe.android.viewmodels.SettingsViewModel
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.animations.smooth
import p4ulor.mediapipe.ui.components.AppIcon
import p4ulor.mediapipe.ui.components.MaterialIcons
import p4ulor.mediapipe.ui.components.utils.BoxWithBackground
import p4ulor.mediapipe.ui.components.utils.SmoothHorizontalDivider
import p4ulor.mediapipe.ui.components.utils.SystemNavigationBarHeight
import p4ulor.mediapipe.ui.components.utils.currentRoute
import p4ulor.mediapipe.ui.components.utils.previousRoute
import p4ulor.mediapipe.ui.screens.about.AboutScreen
import p4ulor.mediapipe.ui.screens.home.HomeScreen
import p4ulor.mediapipe.ui.screens.settings.SettingsScreen
import p4ulor.mediapipe.ui.theme.AppTheme

val BottomNavigationBarHeight = 60.dp

@Composable
fun RootScreen() = Surface { // The surface is used to for theming to work properly
    val navController = rememberNavController()
    var currentScreen by rememberSaveable { mutableStateOf(Screen.Home) }
    var isBottomBarVisible by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    LaunchedEffect(Unit) {
        isBottomBarVisible = true
    }

    val (background, isBgInverted) = if(isSystemInDarkTheme()) {
        when(currentScreen) {
            Screen.Settings -> R.drawable.background_dark_2 to false
            Screen.About -> R.drawable.background_dark_2 to true
            else -> R.drawable.background_dark to false
        }
    } else {
        when(currentScreen) {
            Screen.Home -> R.drawable.background_light to false
            else ->  R.drawable.background_light_2 to false
        }
    }

    val navigateTo: (Screen) -> Unit = {
        currentScreen = it
        navController.navigate(currentScreen.name)
    }

    BoxWithBackground(background, invert = isBgInverted) {
        // I'll keep this VM here for demo/historical purposes, the other VM's are injected with Koin
        val mainVM = viewModel<MainViewModel>(
            factory = create(MainViewModel::class, LocalContext.current.applicationContext)
        )
        val settingsVM = koinViewModel<SettingsViewModel>()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            content = {
                NavHost( // Comes with default fade transitions between routes
                    navController,
                    startDestination = Screen.Home.name,
                    Modifier.padding(it), // Important so that NavHost can make the screens automatically take in consideration the bottom bar
                ) {
                    composable(route = Screen.About.name) { AboutScreen() }
                    composable(route = Screen.Home.name) { HomeScreen(mainVM) }
                    composable(route = Screen.Settings.name) { SettingsScreen(settingsVM) }
                }

                BackHandler { // Should be placed after NavHost, so it's BackHandler is override by this
                    with(navController.currentRoute) {
                        if (this == Screen.Home.name) {
                            ctx.getActivity()?.moveTaskToBack(true) // minimize app
                        } else {
                            navigateTo(Screen.from(navController.previousRoute))
                        }
                    }
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = isBottomBarVisible,
                    enter = fadeIn(smooth())
                ) {
                    SmoothHorizontalDivider()
                    NavigationBar(
                        Modifier.height(SystemNavigationBarHeight + BottomNavigationBarHeight),
                        Color.Transparent
                    ) {
                        bottomBarDestinations.forEach { item ->
                            buildNavigationBarItem(item, currentScreen, onClick = { barItem ->
                                if(currentScreen.name != barItem.screen.name){ // Avoids re-loading the route again
                                    navigateTo(barItem.screen)
                                }
                            })
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun RowScope.buildNavigationBarItem(
    item: NavItem,
    currentScreen: Screen,
    onClick: (item: NavItem) -> Unit,
) = NavigationBarItem(
        selected = currentScreen == item.screen.also { i("Is ${item.screen} selected  =$${currentScreen == item.screen}") },
        onClick = { onClick(item) },
        label = { Text(stringResource(item.screen.nameRes)) },
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
                    contentDescription = item.screen.name,
                    modifier = Modifier.size(item.size)
                )
            }
        }
    )

/**
 * This enum defines the order in which the destinations appear.
 * The names of the enums are also the id of the each screen
 * @param [nameRes] the name used in the navigation bar
 */
enum class Screen(
    @StringRes val nameRes: Int,
    val icon: AppIcon? = null,
    val materialIcon: ImageVector? = null,
    val size: Dp = 25.dp
) {
    About(R.string.about, materialIcon = MaterialIcons.Info, size = 22.dp),
    Home(R.string.home, materialIcon = MaterialIcons.Home),
    Settings(R.string.settings, icon = AppIcon.Settings);

    companion object {
        fun from(string: String?) = Screen.values().first{ it.name == string }
    }
}

/** If it's failing, comment out uses of [LocalContext.current]. Find a solution for this */
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun RootScreenPreview() = AppTheme {
    RootScreen()
}