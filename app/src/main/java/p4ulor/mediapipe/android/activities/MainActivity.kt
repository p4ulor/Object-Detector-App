package p4ulor.mediapipe.android.activities

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.camera.extensions.ExtensionMode
import p4ulor.mediapipe.ui.screens.root.RootScreen

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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        return object : OnBackInvokedDispatcher{
            override fun registerOnBackInvokedCallback(
                priority: Int,
                callback: OnBackInvokedCallback
            ) {}
            override fun unregisterOnBackInvokedCallback(callback: OnBackInvokedCallback) {}
        }
    }

    override fun onBackPressed() {
        return
        super.onBackPressed()
    }
}