package p4ulor.mediapipe.data.sources.gemini

import kotlinx.serialization.Serializable

/** https://ai.google.dev/api/generate-content#request-body */
@Serializable
data class GenerateContentRequest(
    val contents: List<Content>
) {

    constructor(text: String, imageBase64: String, format: MimeTypes) : this(
        listOf(
            Content(
                listOf(Part(text), Part(inline_data = Image(format.value, imageBase64)))
            )
        )
    )

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



