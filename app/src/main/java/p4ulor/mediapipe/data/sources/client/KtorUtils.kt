package p4ulor.mediapipe.data.sources.client

import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.isSuccess

suspend fun <T: HttpResponse, R> T.handle(
    onSuccess: suspend (HttpResponse) -> R,
    onFailure: suspend  (HttpResponse) -> R
) : R {
    return if (status.isSuccess()) {
        onSuccess(this)
    } else {
        onFailure(this)
    }
}

fun HeadersBuilder.addAll(headers: Headers){
    headers.forEach { key, values ->
        appendAll(key, values)
    }
}
