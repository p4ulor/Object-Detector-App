package p4ulor.mediapipe.data.domains.gemini

import p4ulor.mediapipe.data.sources.gemini.MimeTypes

data class GeminiPrompt(
    val text: String,
    val imageBase64: String,
    val format: MimeTypes
)
