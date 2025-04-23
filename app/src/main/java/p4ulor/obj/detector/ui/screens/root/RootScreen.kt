package p4ulor.obj.detector.ui.screens.root

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import p4ulor.obj.detector.R
import p4ulor.obj.detector.android.activities.utils.getActivity
import p4ulor.obj.detector.android.viewmodels.HomeViewModel
import p4ulor.obj.detector.android.viewmodels.utils.create
import p4ulor.obj.detector.ui.animations.smooth
import p4ulor.obj.detector.ui.components.Icon
import p4ulor.obj.detector.ui.components.MaterialIcons
import p4ulor.obj.detector.ui.components.ResourcesIcon
import p4ulor.obj.detector.ui.components.utils.BoxWithBackground
import p4ulor.obj.detector.ui.components.utils.currentRoute
import p4ulor.obj.detector.ui.components.utils.previousRoute
import p4ulor.obj.detector.ui.screens.achievements.AchievementsScreen
import p4ulor.obj.detector.ui.screens.home.HomeScreen
import p4ulor.obj.detector.ui.screens.settings.SettingsScreen

val BottomNavigationBarHeight = 60.dp

@Composable
fun RootScreen() = Surface { // The surface is used to for theming to work properly
    val ctx = LocalContext.current
    val navController = rememberNavController()
    var currentScreen by rememberSaveable { mutableStateOf(Screen.Home) }
    var isBottomBarVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isBottomBarVisible = true
    }

    val navigateTo: (Screen) -> Unit = {
        currentScreen = it
        navController.navigate(currentScreen.name)
    }

    BoxWithBackground(getBackground(currentScreen)) {

        // I'll keep this VM here for demo/historical purposes, the other VM's are injected with Koin
        val homeVM = viewModel<HomeViewModel>(
            factory = create<HomeViewModel>(LocalContext.current.applicationContext)
        )

        Scaffold(
            Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            content = {
                NavHost( // Comes with default fade transitions between routes
                    navController,
                    startDestination = Screen.Home.name,
                    Modifier.padding(it), // Important so that NavHost can make the screens automatically take in consideration the bottom bar
                ) {
                    composable(route = Screen.Achievements.name) { AchievementsScreen() }
                    composable(route = Screen.Home.name) { HomeScreen(homeVM) }
                    composable(route = Screen.Settings.name) { SettingsScreen() }
                }

                BackHandler { // Should be placed after NavHost, so it's BackHandler is overridden by this
                    with(navController.currentRoute) {
                        if (this == Screen.Home.name) {
                            ctx.getActivity()?.moveTaskToBack(true) // minimize app, instead of the default of destroying activity
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
                    BottomBar(currentScreen, onNavigateTo = { screen ->
                        navigateTo(screen)
                    })
                }
            }
        )
    }
}

@Composable
private fun getBackground(currentScreen: Screen) = if(isSystemInDarkTheme()) {
    when(currentScreen) {
        Screen.Home -> R.drawable.background_dark
        else -> R.drawable.background_dark_2
    }
} else {
    when(currentScreen) {
        Screen.Home -> R.drawable.background_light
        else ->  R.drawable.background_light_2
    }
}

/**
 * This enum defines the order in which the destinations appear.
 * The names of the enums are also the id of the each screen
 * @param [nameRes] the name used in the navigation bar
 */
enum class Screen(
    @StringRes val nameRes: Int,
    val icon: Icon,
    val size: Dp = 25.dp
) {
    Achievements(R.string.achievements, Icon.Material(MaterialIcons.WorkspacePremium)),
    Home(R.string.home, Icon.Material(MaterialIcons.Home)),
    Settings(R.string.settings, Icon.App(ResourcesIcon.Settings));

    companion object {
        fun from(string: String?) = Screen.entries.first { it.name == string }
    }
}
