package p4ulor.mediapipe.data.domains.gemini

/**
 * Condensed data of the Gemini API response
 * @param [totalTokensUsed] = promptTokenCount + responseTokenCount
 */
data class GeminiResponse(
    val generatedText: String,
    val totalTokensUsed: String
)