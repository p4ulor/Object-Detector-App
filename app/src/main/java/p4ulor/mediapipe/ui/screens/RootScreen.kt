package p4ulor.mediapipe.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import p4ulor.mediapipe.data.viewmodel.MainViewModel
import p4ulor.mediapipe.data.viewmodel.MainViewModelFactory
import p4ulor.mediapipe.ui.screens.home.HomeScreen
import p4ulor.mediapipe.ui.theme.AppTheme

@Composable
fun RootScreen(){
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(LocalContext.current)
            )

            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Screens.HOME,
            ) {
                composable(route = Screens.HOME) {
                    HomeScreen(viewModel)
                }
            }
        }
    }
}

object Screens {
    const val HOME = "HOME"
}