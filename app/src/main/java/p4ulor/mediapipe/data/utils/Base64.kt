package p4ulor.mediapipe.data.utils

import android.content.Context
import android.net.Uri
import p4ulor.mediapipe.e
import java.util.Base64

/**
 * Given an [Uri] that should be a file, it is converted to a Base64 string or null on error.
 * Note: this doesn't require permission `android.permission.READ_EXTERNAL_STORAGE` because the [Uri]
 * has the format of `content://` which came from the SAF, [Storage Access Framework](https://developer.android.com/guide/topics/providers/document-provider),
 * contrary to getting the file through `file://` protocol.
 */
fun Context.fileToBase64(file: Uri): String? {
    return try {
        val bytes = contentResolver.openInputStream(file)?.use { inputStream ->
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
