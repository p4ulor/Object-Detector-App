package p4ulor.mediapipe.android.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import p4ulor.mediapipe.ui.screens.RootScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lock screen to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        setContent {
            RootScreen()
        }
    }
}