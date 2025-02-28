package p4ulor.mediapipe.data.domains.gemini

import p4ulor.mediapipe.data.sources.cloud.gemini.GenerateContentResponse
import p4ulor.mediapipe.data.utils.getTodaysDate

/**
 * Condensed data of the Gemini API HTTP body response [GenerateContentResponse]
 * @param [totalTokensUsed] = promptTokenCount + responseTokenCount
 */
data class GeminiResponse(
    val generatedText: String,
    val totalTokensUsed: Int
) {
    val date: String = getTodaysDate()
}
