package p4ulor.mediapipe.data.domains.gemini

import p4ulor.mediapipe.data.sources.gemini.MimeType

data class GeminiPrompt(
    val text: String,
    val imageBase64: String,
    val format: MimeType
)
