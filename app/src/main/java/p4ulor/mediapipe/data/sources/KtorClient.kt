package p4ulor.mediapipe.data.sources

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import p4ulor.mediapipe.data.domains.gemini.DummyJsonTest
import p4ulor.mediapipe.data.domains.gemini.DummyJsonTestPostResp

/**
 * An HTTP client with some easy to use methods ready to send and receive data in JSON format
 * Note: [hostName] cannot contain the URL scheme)
 */
class KtorClient(private val hostName: String) {
    private val httpClient = HttpClient {
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
    }

    suspend fun get(path: String, queryParams: QueryParams = emptyList()): DummyJsonTest? {
        val response = httpClient.get {
            withUrl(path, queryParams)
        }
        return if (response.status.isSuccess()) {
            response.body<DummyJsonTest>()
        } else {
            null
        }
    }

    suspend fun post(path: String, body: Any): DummyJsonTestPostResp? {
        val response = httpClient.post {
            withUrlAndBody(path, body = body)
        }
        return if (response.status.isSuccess()) {
            response.body<DummyJsonTestPostResp>()
        } else {
            null
        }
    }

    private fun HttpRequestBuilder.withUrl(
        path: String,
        queryParams: QueryParams = emptyList(),
        extraConfig: URLBuilder.() -> Unit = {}
    ) {
        url {
            contentType(ContentType.Application.Json)
            path(path)
            queryParams.forEach {
                parameters.append(it.first, it.second.toString())
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
}

private typealias QueryParams = List<Pair<String, Any>>