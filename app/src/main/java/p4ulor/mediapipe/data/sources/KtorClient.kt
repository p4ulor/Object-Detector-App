package p4ulor.mediapipe.data.sources

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorClient(val hostUrl: String) {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun get(path: String) {
        val response = httpClient.get {
            url {
                contentType(ContentType.Application.Json)
                path(path)
            }
        }
        if (response.status.isSuccess()) {
            println("RESPONSE: ${response.bodyAsText()}")
        }
    }
}