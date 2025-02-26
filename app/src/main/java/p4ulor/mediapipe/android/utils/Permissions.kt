package p4ulor.mediapipe.android.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import p4ulor.mediapipe.R

/**
 * https://developer.android.com/training/permissions/requesting#explain
 * https://composables.com/jetpack-compose-tutorials/permissions
 * https://developer.android.com/about/versions/11/privacy/permissions
 * https://stackoverflow.com/questions/67825724/how-to-ask-again-for-permission-if-it-was-denied-in-android
 */
fun Activity.requestPermission(permission: String){
    val hasPermission = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    if(!hasPermission){
        ActivityCompat.requestPermissions(this, arrayOf(permission), 0)
    }
}

/** See docs of [requestPermission] */
fun Context.requestUserToManuallyAddThePermission(){
    AlertDialog.Builder(this)
        .setTitle(getString(R.string.camera_permission_needed))
        .setMessage(getString(R.string.camera_permission_needed_text))
        .setPositiveButton("Ok") { _, _ ->
            val goToSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(goToSettingsIntent)
        }.show()
}
