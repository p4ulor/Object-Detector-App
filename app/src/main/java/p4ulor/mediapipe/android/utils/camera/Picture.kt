package p4ulor.mediapipe.android.utils.camera

import android.content.Context
import android.net.Uri
import p4ulor.mediapipe.data.sources.cloud.gemini.MimeType
import p4ulor.mediapipe.data.utils.fileToBase64

sealed class Picture private constructor(val mimeType: MimeType = ImageCaptureDefault.mimeType) {
    /**
     * @property path should be something like `content://media/external/images/media/1000069851`
     */
    data class File(val path: Uri) : Picture()
    /** Used when the Picture is only to be in memory and was not saved to a file */
    data class Base64(val base64: String) : Picture()

    val asFile get() = this as? File
    val asBase64 get() = this as? Base64

    context(Context)
    fun imageAsBase64(): String? =
        this.asFile?.path?.let { fileToBase64(it) }
            ?: this.asBase64?.base64
}
