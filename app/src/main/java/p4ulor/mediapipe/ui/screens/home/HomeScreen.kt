package p4ulor.mediapipe.ui.screens.home

import android.Manifest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.utils.getActivity
import p4ulor.mediapipe.android.utils.getCameraProvider
import p4ulor.mediapipe.android.utils.requestPermission
import p4ulor.mediapipe.android.utils.requestUserToManuallyAddThePermission
import p4ulor.mediapipe.android.viewmodels.HomeViewModel
import p4ulor.mediapipe.data.storage.preferences.UserPreferences
import p4ulor.mediapipe.data.storage.preferences.UserSecretPreferences
import p4ulor.mediapipe.ui.components.QuickText
import p4ulor.mediapipe.ui.components.utils.CenteredContent
import p4ulor.mediapipe.ui.components.utils.requestPermission

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val ctx = LocalContext.current

    val isGranted = requestPermission(Manifest.permission.CAMERA, onPermissionNotGranted = {
        CenteredContent {
            /** See [requestPermission] */
            var oneTimePermRequestWasUsed by rememberSaveable { mutableStateOf(false) }
            QuickText(R.string.no_camera_permission)
            Button(onClick = {
                if(!oneTimePermRequestWasUsed){
                    ctx.getActivity()?.requestPermission()
                    oneTimePermRequestWasUsed = true
                } else {
                    ctx.requestUserToManuallyAddThePermission()
                }
            }) {
                QuickText(R.string.get_permissions)
            }
        }
    })

    if(isGranted){
        var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
        var prefs by remember { mutableStateOf<UserPreferences?>(null) }
        var secretPrefs by remember { mutableStateOf<UserSecretPreferences?>(null) }

        LaunchedEffect(Unit) {
            delay(500) // To let the initial launch animations to breathe
            prefs = viewModel.loadUserPrefs().first() // Loads prefs which need to be obtained everytime if user changed them in Settings
            secretPrefs = viewModel.loadUserSecretPrefs().first()
            cameraProvider = ctx.getCameraProvider()
        }

        if(cameraProvider!=null && prefs!=null && secretPrefs!=null) {
            HomeScreenGranted(viewModel, cameraProvider!!, prefs!!)
        } else {
            CenteredContent {
                CircularProgressIndicator(Modifier.size(100.dp))
            }
        }
    }
}
