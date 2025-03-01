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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.activities.utils.getActivity
import p4ulor.mediapipe.android.utils.camera.getCameraProvider
import p4ulor.mediapipe.android.utils.requestPermission
import p4ulor.mediapipe.android.utils.requestUserToManuallyAddThePermission
import p4ulor.mediapipe.android.viewmodels.HomeViewModel
import p4ulor.mediapipe.data.sources.local.preferences.UserPreferences
import p4ulor.mediapipe.data.sources.local.preferences.UserSecretPreferences
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.components.QuickText
import p4ulor.mediapipe.ui.components.utils.CenteredContent
import p4ulor.mediapipe.ui.theme.PreviewComposable

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val ctx = LocalContext.current
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var prefs by remember { mutableStateOf<UserPreferences?>(null) }
    var secretPrefs by remember { mutableStateOf<UserSecretPreferences?>(null) }

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(cameraPermission.isGranted) {
        if(cameraPermission.isGranted){
            delay(100) // Gives time for CircularProgressIndicator and NavigationBar animations to show
            launch { cameraProvider = ctx.getCameraProvider() }
            launch { prefs = viewModel.loadUserPrefs().first() } // Loads prefs which need to be obtained everytime if user changed them in Settings
            launch { secretPrefs = viewModel.loadUserSecretPrefs().first() }
        }
    }

    if(cameraPermission.isGranted){
        if (cameraProvider != null && prefs != null && secretPrefs != null) {
            HomeScreenGranted(viewModel, cameraProvider!!, prefs!!)
        } else {
            CenteredContent {
                CircularProgressIndicator(Modifier.size(100.dp))
            }
        }
    } else {
        HomeScreenNotGranted()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
private val PermissionState.isGranted: Boolean
    get() = status.isGranted

@Composable
private fun HomeScreenNotGranted() {
    val ctx = LocalContext.current
    var oneTimePermRequestWasUsed by rememberSaveable { mutableStateOf(false) }

    CenteredContent {
        QuickText(R.string.no_camera_permission)

        Button(onClick = {
            if(!oneTimePermRequestWasUsed){
                ctx.getActivity()?.requestPermission(Manifest.permission.CAMERA)
                oneTimePermRequestWasUsed = true
            } else {
                ctx.requestUserToManuallyAddThePermission()
            }
        }) {
            QuickText(R.string.get_permissions)
        }
    }
}

@Preview
@Composable
private fun HomeScreenNotGrantedPreview() = PreviewComposable {
    HomeScreenNotGranted()
}
