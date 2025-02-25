package p4ulor.mediapipe.data.sources.gemini

import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import p4ulor.mediapipe.data.domains.gemini.GeminiPrompt
import p4ulor.mediapipe.data.domains.gemini.GeminiResponse
import p4ulor.mediapipe.data.sources.KtorClient
import p4ulor.mediapipe.data.sources.gemini.GeminiApiEndpoints.Companion.defaultHeaders
import p4ulor.mediapipe.data.sources.utils.getIfOkOrNull
import p4ulor.mediapipe.data.utils.getTodaysDate
import p4ulor.mediapipe.e
import java.io.Closeable

/**
 * Provides a clean interface for making HTTP calls to the [GeminiApiEndpoints] using a [KtorClient].
 * The methods receive Data Structures (DS) from [data.domains.gemini], then obtain DS
 * from [data.sources.gemini] and converts them to DS from [data.domains.gemini]
 * See "gemini test2.sh" in docs/gemini-api-curl-test for reference.
 * @returns a [GeminiResponse] or null if there was some internal error in the request or related
 * to [http] KtorClient
 */
class GeminiApiService(apiKey: String) : Closeable {
    private val http = KtorClient(GeminiApiEndpoints.hostName)
    private val endpoints = GeminiApiEndpoints(apiKey)

    suspend fun promptWithImage(prompt: GeminiPrompt): GeminiResponse? {
        val (path, queryParams) = endpoints.postTo(GeminiApiEndpoints.Resources.Models.GenerateContent)
        val body = with(prompt) { GenerateContentRequest(text, imageBase64, format) }

        return runCatching {
            http.post(path, queryParams, body, defaultHeaders)?.let { response ->

                if(response.status.isSuccess()){
                    response.body<GenerateContentResponse>().let {
                        GeminiResponse(
                            generatedText = it.candidates.first().content.parts.first().text,
                            totalTokensUsed = it.usageMetadata.totalTokenCount,
                            getTodaysDate()
                        )
                    }
                } else { //todo handle more properly
                    e("promptWithImage error: ${response.bodyAsText()}")
                    GeminiResponse("Error", 0, "")
                }

            }
        }.onFailure {
            e("promptWithImage stacktrace: ${it.stackTrace.asList()}")
        }.getOrNull()
    }

    override fun close() = http.close()
}