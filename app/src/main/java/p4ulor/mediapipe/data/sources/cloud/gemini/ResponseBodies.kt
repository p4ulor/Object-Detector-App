package p4ulor.mediapipe.data.sources.cloud.gemini

import kotlinx.serialization.Serializable

/** https://ai.google.dev/api/generate-content#v1beta.GenerateContentResponse */
@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>,
    val usageMetadata: UsageMetadata,
    val modelVersion: String
) {
    @Serializable
    data class Candidate(
        val content: Content,
        val finishReason: String,
        val avgLogprobs: Double
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
}
