package p4ulor.obj.detector.ui.screens.home

import android.Manifest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Spacer
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
import p4ulor.obj.detector.R
import p4ulor.obj.detector.android.activities.utils.getActivity
import p4ulor.obj.detector.android.utils.camera.getCameraProvider
import p4ulor.obj.detector.android.utils.requestPermission
import p4ulor.obj.detector.android.utils.requestUserToManuallyAddThePermission
import p4ulor.obj.detector.android.viewmodels.HomeViewModel
import p4ulor.obj.detector.data.sources.local.preferences.UserPreferences
import p4ulor.obj.detector.ui.components.EzText
import p4ulor.obj.detector.ui.components.utils.CenteredColumn
import p4ulor.obj.detector.ui.components.utils.GeneralPadding
import p4ulor.obj.detector.ui.theme.PreviewComposable

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val ctx = LocalContext.current
    val notificationsPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA, onPermissionResult = { isPermissionGranted ->
        if(isPermissionGranted){
            notificationsPermission.launchPermissionRequest()
        }
    })
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var prefs by remember { mutableStateOf<UserPreferences?>(null) }

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(cameraPermission.isGranted) {
        if (cameraPermission.isGranted) {
            delay(200) // Gives time for CircularProgressIndicator animation to show
            withContext(Dispatchers.Default){ // So the UI thread is not used and 2 coroutines are used to try to load ASAP (no need to put in the VM to add more noise)
                launch { cameraProvider = ctx.getCameraProvider() } // No, these shouldn't be in the VM, since the camera should be unbinded when we leave HomeScreenGranted
                launch { prefs = viewModel.loadAndGetUserPrefs().first() } // Loads prefs which need to be obtained everytime if user changed them in Settings
            }
        }
    }

    if (cameraPermission.isGranted) {
        if (cameraProvider != null && prefs != null) {
            HomeScreenGranted(viewModel, cameraProvider!!, prefs!!)
        } else {
            CenteredColumn {
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

    CenteredColumn {
        EzText(R.string.no_camera_permission)
        Spacer(Modifier.size(GeneralPadding))
        Button(onClick = {
            if (!oneTimePermRequestWasUsed) {
                ctx.getActivity()?.requestPermission(Manifest.permission.CAMERA)
                ctx.getActivity()?.requestPermission(Manifest.permission.POST_NOTIFICATIONS)
                oneTimePermRequestWasUsed = true
            } else {
                ctx.requestUserToManuallyAddThePermission()
            }
        }) {
            EzText(R.string.get_permissions)
        }
    }
}

@Preview
@Composable
private fun HomeScreenNotGrantedPreview() = PreviewComposable {
    HomeScreenNotGranted()
}
