package p4ulor.obj.detector.android.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import p4ulor.obj.detector.android.MyApplication
import p4ulor.obj.detector.data.sources.local.database.initializeDb
import p4ulor.obj.detector.i
import p4ulor.obj.detector.ui.screens.root.RootScreen
import p4ulor.obj.detector.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        i("onCreate")

        // Lock screen to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        // Initialize DB
        val app = application as MyApplication
        lifecycleScope.launch(Dispatchers.IO) {
            initializeDb(app.appDb)
        }

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
