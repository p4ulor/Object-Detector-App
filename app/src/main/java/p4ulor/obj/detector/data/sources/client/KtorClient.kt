package p4ulor.obj.detector.data.sources.client

import io.ktor.client.HttpClient
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
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.Configuration
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.toMap
import kotlinx.serialization.json.Json
import p4ulor.obj.detector.e
import p4ulor.obj.detector.i
import java.io.Closeable

/**
 * An HTTP client with some easy to use methods ready to send and receive data in JSON format via
 * [Configuration.json]. All methods can throw exception if the [HttpStatusCode] is not ok
 *
 * Notes:
 * - [hostName] cannot contain the URL scheme/protocol, which is defined in [DefaultRequest] block
 * - [QueryParams] cannot be in the path when calling [get], [post], etc. They need to be specially
 * encoded by Ktor at [withUrl]
 * - Consider using [close] when closing the app
 * - Maybe todo: Support streaming responses
 *      - https://ktor.io/docs/client-responses.html#streaming
 *      - https://ai.google.dev/api/generate-content#method:-models.streamgeneratecontent
 */
class KtorClient(private val hostName: String) : Closeable {
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
            //requestTimeoutMillis = 10000 // this timeout sometimes expired IDK why, Ktor or Gemini bug?
        }
        install(HttpRequestRetry){
            delayMillis { retryCount ->
                i("Retrying HTTP request")
                1000L
            }
            retryOnServerErrors(maxRetries = 1)
        }
        HttpResponseValidator {
            // This is used like a response interceptor. Alternative for logging: install(CallLogging)
            // Note: body can only be read once! I've tried to log it here, but I'm not stressing
            // about it. The body should be read by the GeminiApiService using this KtorClient
            validateResponse { response ->
                val responseLog = with(response){
                    val url = response.call.request.url
                    val method = response.call.request.method
                    val contentType = headers.toMap().get(HttpHeaders.ContentType).toString()
                    "KtorClient: status=$status, url=$url, method=$method, contentType=$contentType"
                }
                if(response.status.isSuccess()) {
                    i(responseLog)
                } else {
                    e(responseLog)
                }
            }
        }
    }

    suspend fun get(
        path: String,
        queryParams: QueryParams = emptyList(),
        headers: Headers = headers {}
    ): HttpResponse {
        val response = httpClient.get {
            withUrl(path, queryParams)
            headers { addAll(headers) }
        }
        return response
    }

    suspend fun post(
        path: String,
        queryParams: QueryParams = emptyList(),
        body: Any,
        headers: Headers = headers {}
    ): HttpResponse {
        val response = httpClient.post {
            withUrl(path, queryParams, extraConfig = { setBody(body) })
            headers { addAll(headers) }
        }
        return response
    }

    override fun close() = httpClient.close()

    /** Util function that should be common to all HTTP-method functions */
    private fun HttpRequestBuilder.withUrl(
        path: String,
        queryParams: QueryParams = emptyList(),
        extraConfig: URLBuilder.() -> Unit = {}
    ) {
        url {
            contentType(defaultContentType)
            path(path)
            queryParams.forEach {
                parameters.append(it.first, it.second)
            }
            extraConfig()
        }
    }

    companion object {
        val defaultContentType = ContentType.Application.Json
    }
}

