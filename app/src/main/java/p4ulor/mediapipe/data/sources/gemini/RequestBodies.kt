package p4ulor.mediapipe.data.sources.gemini

import kotlinx.serialization.Serializable

/**
 * HTTP method: POST
 * https://ai.google.dev/gemini-api/docs/vision?lang=rest#upload-image
 */
 object UploadImageAndGenerateContentRequest {
     /** Step 1 */
    @Serializable
    data class UploadImage(
        val file: FileInfo
    )

    @Serializable
    data class GenerateContentRequest(
        val contents: List<Content>
    )

    @Serializable
    data class Content(
        val parts: List<Part>
    )

    @Serializable
    data class Part(
        val text: String?,
        val file_data: FileData?
    )

    @Serializable
    data class FileData(
        val mime_type: String,
        val file_uri: String
    )

    // Sub-objects

    @Serializable
    data class FileInfo(
        val display_name: String
    )
 }



