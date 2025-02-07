package p4ulor.mediapipe.android.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import p4ulor.mediapipe.R

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
