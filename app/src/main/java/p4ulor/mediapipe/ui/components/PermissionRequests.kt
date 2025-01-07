package p4ulor.mediapipe.ui.components

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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

fun Context.requestUserToManuallyAddThePermission(){
    AlertDialog.Builder(this)
        .setTitle("Camera Permission Needed")
        .setMessage("This app needs access to your camera to take photos.")
        .setPositiveButton("Ok") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
        }.show()
}
