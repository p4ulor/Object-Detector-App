package p4ulor.mediapipe.data.sources.cloud.gemini

import kotlinx.serialization.Serializable

/** https://ai.google.dev/api/generate-content#request-body */
@Serializable
data class GenerateContentRequest(
    val contents: List<Content>
) {

    @Serializable
    data class Content(
        val parts: List<Part>
    )

    @Serializable
    data class Part(
        val text: String? = null,
        val inline_data: Image? = null
    )

    @Serializable
    data class Image(
        val mime_type: String,
        val data: String
    )
 }
