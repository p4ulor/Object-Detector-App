package p4ulor.mediapipe.data.sources.gemini

import kotlinx.serialization.Serializable

/** https://ai.google.dev/gemini-api/docs/vision?lang=rest#upload-image */
@Serializable
data class UploadedImageGeneratedContentResponse(
    val candidates: List<Candidate>,
    val usageMetadata: UsageMetadata,
    val modelVersion: String
)

// Sub-objects

@Serializable
data class Candidate(
    val content: Content,
    val finishReason: String,
    val avgLogprobs: Float
)

@Serializable
data class Content(
    val parts: List<Part>,
    val role: String
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class UsageMetadata(
    val promptTokenCount: Int,
    val candidatesTokenCount: Int,
    val totalTokenCount: Int
)