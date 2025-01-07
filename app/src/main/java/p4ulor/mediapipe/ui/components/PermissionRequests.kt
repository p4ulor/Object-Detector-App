package p4ulor.mediapipe.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

/**
 * @returns true if permission was granted, false otherwise
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun requestPermission(permission: String, onPermissionNotGranted: @Composable () -> Unit) : Boolean {
    val cameraPermission = rememberPermissionState(permission)
    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }
    val isGranted = cameraPermission.status.isGranted
    if (!isGranted) {
        onPermissionNotGranted()
    }
    return isGranted
}
