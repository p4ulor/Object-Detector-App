package p4ulor.mediapipe.data.utils

import android.content.Context
import p4ulor.mediapipe.android.utils.camera.Picture
import p4ulor.mediapipe.e
import java.util.Base64

/**
 * Converts a [Picture] to a string in Base64 or null on error
 * Fun fact: this doesn't require permission `android.permission.READ_EXTERNAL_STORAGE`
 */
fun Context.uriToBase64(picture: Picture): String? {
    return try {
        val bytes = contentResolver.openInputStream(picture.path)?.use { inputStream ->
            inputStream.readBytes()
        }
        bytes?.let {
            Base64.getEncoder().encodeToString(it)
        }
    } catch (e: Exception) {
        e("uriToBase64 error: ${e.printStackTrace()}")
        null
    }
}

fun encodeToBase64(bytes: ByteArray): String =
    Base64.getEncoder().withoutPadding().encodeToString(bytes)
