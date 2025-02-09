package p4ulor.mediapipe.android.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.screens.root.RootScreen
import p4ulor.mediapipe.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lock screen to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        setContent {
            AppTheme {
                enableEdgeToEdge() // Gives transparent look by default
                RootScreen()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        i("Stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        i("Destroyed")
    }
}