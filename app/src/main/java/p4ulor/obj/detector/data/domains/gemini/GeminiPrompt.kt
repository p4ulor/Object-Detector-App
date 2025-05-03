package p4ulor.obj.detector.data.domains.gemini

import p4ulor.obj.detector.android.utils.camera.ImageCaptureDefault
import p4ulor.obj.detector.data.sources.cloud.gemini.MimeType

data class GeminiPrompt(
    val text: String,
    val imageBase64: String,
    val imageFormat: MimeType = ImageCaptureDefault.mimeType
)
