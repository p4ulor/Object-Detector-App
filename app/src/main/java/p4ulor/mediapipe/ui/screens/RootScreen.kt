package p4ulor.mediapipe.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import p4ulor.mediapipe.ui.theme.MLonAndroidwMediaPipeTheme

@Composable
fun RootScreen(){
    MLonAndroidwMediaPipeTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Screens.HOME,
            ) {
                composable(route = Screens.HOME) {
                    HomeScreen()
                }
            }
        }
    }
}

object Screens {
    const val HOME = "HOME"
}