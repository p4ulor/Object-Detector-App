package p4ulor.mediapipe.android.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * https://developer.android.com/training/permissions/requesting#explain
 * https://composables.com/jetpack-compose-tutorials/permissions
 * https://stackoverflow.com/questions/67825724/how-to-ask-again-for-permission-if-it-was-denied-in-android
 */
fun Activity.requestPermission(permission: String = Manifest.permission.CAMERA){
    val hasPermission = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    if(!hasPermission){
        ActivityCompat.requestPermissions(this, arrayOf(permission), 0)
    }
}
