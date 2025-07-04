package p4ulor.obj.detector.data.sources.cloud.gemini

import io.ktor.client.HttpClient
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.headers
import p4ulor.obj.detector.data.sources.client.KtorClient
import p4ulor.obj.detector.data.sources.client.QueryParam
import p4ulor.obj.detector.data.sources.client.QueryParams
import p4ulor.obj.detector.data.sources.cloud.gemini.GeminiApiEndpoints.Companion.version
import p4ulor.obj.detector.data.sources.cloud.gemini.GeminiApiEndpoints.Endpoint

/**
 * The Gemini HTTP endpoints used to send prompts. It also includes the headers used and
 * details about the HTTP API. Each [Endpoint] must belong to a [Resource](https://www.ibm.com/docs/en/was/8.5.5?topic=applications-defining-resource-methods-restful),
 * which defines a path for specific functionalities or use-cases.
 * [version] is used before the resource paths
 *
 * I decided not to use Ktor's ["Type-safe requests"](https://ktor.io/docs/2.3.13/client-resources.html#resource_path_param)
 * since it requires those annotations and the "parent" property, which comes with many repetitive lines.
 * I also didn't like how calling `httpClient.get` received a generic T and didn't go well with my
 * [KtorClient]. So I did this to have more customization
 *
 * - For the various REST Resources -> https://ai.google.dev/api/all-methods
 * - https://ai.google.dev/api/files
 * - https://ai.google.dev/api/generate-content
 * - https://ai.google.dev/gemini-api/docs/api-versions#rest
 *
 * Get your API key [here](https://aistudio.google.com/app/apikey)
 */
class GeminiApiEndpoints(private val apiKey: String) {
    /** Represents the root path ("/") of the RESTful API resources */
    sealed interface Resources {
        /** https://ai.google.dev/api/all-methods#rest-resource:-v1beta.models */
        sealed interface Models {
            /** https://ai.google.dev/api/generate-content#method:-models.generatecontent */
            object Flash2_0 {
                object GenerateContent : Endpoint("/models/$flash2_0_Model:generateContent", HttpMethod.Post)
            }

            companion object {
                const val flash2_0_Model: String = "gemini-2.0-flash"
            }
        }
    }

    abstract class Endpoint(endpointPath: String, val method: HttpMethod) {
        val path = "$baseBath$endpointPath"
    }

    /**
     * Util method to extract the HTTP URI path and queryParams (which will have the [apiKey]).
     * @return a [Pair] where [Pair.first] = path and [Pair.second] = [QueryParams]. Read [KtorClient]
     */
    fun postTo(endpoint: Endpoint) = run {
        require(endpoint.method == HttpMethod.Post)
        Pair(endpoint.path, listOf(QueryParam(QueryKey.key.name, apiKey)))
    }

    companion object {
        /** Should only be used to configure [HttpClient] */
        const val hostName = "generativelanguage.googleapis.com"
        private const val version = "v1beta" // keep up with this https://ai.google.dev/gemini-api/docs/api-versions
        private const val baseBath = "/$version"
        val defaultHeaders = headers {
            append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }

    private enum class QueryKey {
        key
    }
}

/** https://ai.google.dev/gemini-api/docs/vision?lang=rest#technical-details-image */
enum class MimeType(val value: String) {
    PNG(ContentType.Image.PNG.toString()),
    JPEG(ContentType.Image.JPEG.toString()),
    WEBP("image/webp"),
    HEIC("image/heic"),
    HEIF("image/heif")
}
