package p4ulor.mediapipe.data.sources.gemini

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import p4ulor.mediapipe.data.sources.KtorClient
import p4ulor.mediapipe.data.sources.QueryParams

/**
 * The Gemini HTTP endpoints used to send prompts. It also includes the various headers and
 * details about the HTTP API.
 *
 * I decided not to use Ktor's ["Type-safe requests"](https://ktor.io/docs/2.3.13/client-resources.html#resource_path_param)
 * since it requires those annotations and the "parent" property, which comes with many repetitive lines.
 * I also didn't like how calling `httpClient.get` received a generic T and didn't go well with my
 * [KtorClient]. So I did this to have more customization
 *
 * - https://ai.google.dev/api/all-methods
 * - https://ai.google.dev/api/files
 * - https://ai.google.dev/gemini-api/docs/api-versions#rest
 */
sealed class GeminiApiEndpoints {
    val path = StringBuilder()
    private constructor(path: String){
        this.path.append(path)
    }

    open fun withQueryParams(params: QueryParams): String {
        return if (params.isNotEmpty()) {
            val queryKeys = params.joinToString("&") { "${it.first}=${it.second}" }
            "$path?$queryKeys"
        } else {
            path.toString()
        }
    }

    sealed class Upload(path: String) : GeminiApiEndpoints("/upload/$version/$path") {
        class Files : Upload("/files")
    }

    companion object {
        const val hostName = "generativelanguage.googleapis.com"
        private const val version = "v1beta" // update this
    }
}

private fun uploadConfigFileHeaders(numBytes: Int, mimeType: String) = headers {
    with(GoogleUploadHeader){
        append(Protocol, "resumable")
        append(Command, "start")
        append(ContentLength, numBytes.toString())
        append(ContentType, mimeType)
    }
    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
}

private fun uploadFileHeaders(numBytes: Int) = headers {
    with(GoogleUploadHeader){
        append(Offset, "0")
        append(Command, "upload, finalize")
    }
    append(HttpHeaders.ContentLength, numBytes.toString())
}

private object GoogleUploadHeader {
    const val Protocol = "X-Goog-Upload-Protocol"
    const val Command = "X-Goog-Upload-Command"
    const val ContentLength = "X-Goog-Upload-Header-Content-Length"
    const val ContentType = "X-Goog-Upload-Header-Content-Type"
    const val Offset = "X-Goog-Upload-Offset"
}

/** https://ai.google.dev/gemini-api/docs/vision?lang=rest#technical-details-image */
enum class SupportedMimeTypes(val value: String) {
    PNG(ContentType.Image.PNG.toString()),
    JPEG(ContentType.Image.JPEG.toString()),
    WEBP("image/webp"),
    HEIC("image/heic"),
    HEIF("image/heif")
}
