package p4ulor.mediapipe.data.sources.gemini

import io.ktor.client.call.body
import p4ulor.mediapipe.data.domains.gemini.GeminiPrompt
import p4ulor.mediapipe.data.domains.gemini.GeminiResponse
import p4ulor.mediapipe.data.sources.KtorClient
import p4ulor.mediapipe.data.sources.gemini.GeminiApiEndpoints.Companion.defaultHeaders
import p4ulor.mediapipe.data.utils.getTodaysDate
import p4ulor.mediapipe.e
import java.io.Closeable

/**
 * Provides a clean interface for making HTTP calls to the [GeminiApiEndpoints] using a [KtorClient].
 * The methods receive Data Structures (DS) from [data.domains.gemini], then obtain DS
 * from [data.sources.gemini] and converts them to DS from [data.domains.gemini]
 * See "gemini test2.sh" in docs/gemini-api-curl-test for reference.
 */
class GeminiApiService(apiKey: String) : Closeable {
    private val http = KtorClient(GeminiApiEndpoints.hostName)
    private val endpoints = GeminiApiEndpoints(apiKey)

    suspend fun promptWithImage(prompt: GeminiPrompt): GeminiResponse? {
        val (path, queryParams) = endpoints.postTo(GeminiApiEndpoints.Resources.Models.GenerateContent)
        val body = with(prompt) { GenerateContentRequest(text, imageBase64, format) }
        return runCatching {
            http.post(path, queryParams, body, defaultHeaders)?.let { response ->
                response.body<GenerateContentResponse>().let {
                    GeminiResponse(
                        generatedText = it.candidates.first().content.parts.first().text,
                        totalTokensUsed = it.usageMetadata.totalTokenCount,
                        getTodaysDate()
                    )
                }
            }
        }.onFailure {
            e("promptWithImage stacktrace: ${it.stackTrace.asList()}")
        }.getOrNull()
    }

    override fun close() = http.close()
}