package p4ulor.mediapipe.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import p4ulor.mediapipe.i

/**
 * @returns true if permission was granted, false otherwise
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun requestPermission(permission: String, onPermissionNotGranted: @Composable () -> Unit) : Boolean {
    val cameraPermission = rememberPermissionState(permission)
    LaunchedEffect(Any()) {
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

fun Activity.requestPermission(permission: String = Manifest.permission.CAMERA){
    val hasPermission = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    if(!hasPermission){
        ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
    }
}

@Composable
fun onComposableDisposed(cleanup: () -> Unit){
    DisposableEffect(Any()) {
        onDispose {
            cleanup()
        }
    }
}

@Composable
fun CenteredText(text: String){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text)
        i("Permission not granted")
    }
}

fun Context.getActivityOrNull(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }

    return null
}
