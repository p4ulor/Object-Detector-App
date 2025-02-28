package p4ulor.mediapipe.data.sources.gemini

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import p4ulor.mediapipe.data.domains.gemini.GeminiPrompt
import p4ulor.mediapipe.data.domains.gemini.GeminiResponse
import p4ulor.mediapipe.data.sources.KtorClient
import p4ulor.mediapipe.data.sources.gemini.GeminiApiEndpoints.Companion.defaultHeaders
import p4ulor.mediapipe.data.sources.gemini.GenerateContentRequest.Content
import p4ulor.mediapipe.data.sources.gemini.GenerateContentRequest.Image
import p4ulor.mediapipe.data.sources.gemini.GenerateContentRequest.Part
import p4ulor.mediapipe.e
import java.io.Closeable

/**
 * Provides a clean interface for making HTTP calls to the [GeminiApiEndpoints] using a [KtorClient]
 * and handles Data Structure (DS) conversions between domain and HTTP bodies.
 * The methods receive DSs from [data.domains.gemini], and use DSs from [data.sources.gemini]
 * to interact with [KtorClient] and then converts them back to DS from [data.domains.gemini]
 * See "gemini test2.sh" in docs/gemini-api-curl-test for reference.
 * @returns a [GeminiResponse] or null if there was some internal error in the request or related
 * to [http] KtorClient
 */
class GeminiApiService(apiKey: String) : Closeable {
    private val http = KtorClient(GeminiApiEndpoints.hostName)
    private val endpoints = GeminiApiEndpoints(apiKey)

    suspend fun promptWithImage(prompt: GeminiPrompt): GeminiResponse? {
        val (path, queryParams) = endpoints.postTo(GeminiApiEndpoints.Resources.Models.GenerateContent)
        val body = prompt.toHttpRequest()

        return runCatching {
            http.post(path, queryParams, body, defaultHeaders)?.let { response ->
                if(response.status.isSuccess()){
                    response.body<GenerateContentResponse>().toDomain()
                } else {
                    response.toDomainError().also {
                        e("promptWithImage error: $it")
                    }
                }
            }
        }.onFailure {
            e("promptWithImage stacktrace: ${it.stackTrace.asList()}")
        }.getOrNull()
    }

    override fun close() = http.close()

    private fun GeminiPrompt.toHttpRequest() = GenerateContentRequest(
        listOf(
            Content(
                listOf(Part(text), Part(inline_data = Image(format.value, imageBase64)))
            )
        )
    )

    private fun GenerateContentResponse.toDomain() = GeminiResponse(
        generatedText = candidates.first().content.parts.first().text.trimEnd(), // trimEnd because some answers end with \n
        totalTokensUsed = usageMetadata.totalTokenCount
    )

    private suspend fun HttpResponse.toDomainError() = GeminiResponse(
        generatedText = "Error: ${bodyAsText()}. Status: ${status.description}",
        totalTokensUsed = 0
    )
}