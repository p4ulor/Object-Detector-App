package p4ulor.obj.detector.data.domains.gemini

import p4ulor.obj.detector.data.sources.cloud.gemini.GenerateContentResponse
import p4ulor.obj.detector.data.utils.getTodaysDate

/**
 * Condensed data of the Gemini API HTTP body response [GenerateContentResponse]
 * @param [totalTokensUsed] = promptTokenCount + responseTokenCount
 */
data class GeminiResponse(
    val generatedText: String,
    val totalTokensUsed: Int
) {
    val date: String = getTodaysDate()
    val rawGeneratedText: String
        get() = generatedText
            .replace("\r", "\\r")
            .replace("\n", "\\n")
}
