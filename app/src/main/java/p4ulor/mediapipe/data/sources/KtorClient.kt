package p4ulor.mediapipe.data.sources

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import p4ulor.mediapipe.data.sources.gemini.DummyJsonTest
import p4ulor.mediapipe.data.sources.gemini.DummyJsonTestPostResp
import p4ulor.mediapipe.i

/**
 * An HTTP client with some easy to use methods ready to send and receive data in JSON format
 * Notes:
 * - [hostName] cannot contain the URL scheme)
 * - Consider using [close] when closing the app
 */
class KtorClient(private val hostName: String) {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = hostName
            }
        }
        install(HttpTimeout){
            requestTimeoutMillis = 10000
        }
        install(HttpRequestRetry){
            delayMillis { retryCount ->
                i("Retrying HTTP request")
                1000L
            }
            retryOnServerErrors(maxRetries = 1)
        }
        HttpResponseValidator {
            validateResponse { response -> // This is a like a response interceptor
                if(!response.status.isSuccess()){
                    i("KtorClient: status: ${response.status} error: ${response.bodyAsText()}")
                }
            }
        }
    }

    suspend fun get(
        path: String,
        queryParams: QueryParams = emptyList(),
        headers: Headers = headers {}
    ): DummyJsonTest? {
        val response = httpClient.get {
            withUrl(path, queryParams)
            headers { addAll(headers) }
        }

        return if (response.status.isSuccess()) {
            response.body<DummyJsonTest>()
        } else {
            null
        }
    }

    suspend fun post(
        path: String,
        body: Any, 
        headers: Headers = headers {}
    ): DummyJsonTestPostResp? {
        val response = httpClient.post {
            withUrlAndBody(path, body = body)
            headers { addAll(headers) }
        }
        return if (response.status.isSuccess()) {
            response.body<DummyJsonTestPostResp>()
        } else {
            null
        }
    }

    fun close() = httpClient.close()

    private fun HttpRequestBuilder.withUrl(
        path: String,
        queryParams: QueryParams = emptyList(),
        extraConfig: URLBuilder.() -> Unit = {}
    ) {
        url {
            contentType(ContentType.Application.Json)
            path(path)
            queryParams.forEach {
                parameters.append(it.first, it.second)
            }
            extraConfig()
        }
    }

    private fun HttpRequestBuilder.withUrlAndBody(
        path: String,
        queryParams: QueryParams = emptyList(),
        body: Any
    ) = withUrl(path, queryParams, extraConfig = {
        contentType(ContentType.Application.Json)
        setBody(body)
    })

    private fun HeadersBuilder.addAll(headers: Headers){
        headers.forEach { key, values ->
            appendAll(key, values)
        }
    }
}

typealias QueryParams = List<Pair<String, String>>