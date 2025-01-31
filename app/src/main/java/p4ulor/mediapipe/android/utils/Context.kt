package p4ulor.mediapipe.android.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

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