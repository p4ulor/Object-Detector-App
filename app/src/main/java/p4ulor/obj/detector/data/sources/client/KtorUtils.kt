package p4ulor.obj.detector.data.sources.client

import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.isSuccess

/**
 * [R] a type of a [HttpResponse]
 * [D] a type of a Domain
 */
suspend fun <R: HttpResponse, D> R.handle(
    onSuccess: suspend (HttpResponse) -> D,
    onFailure: suspend (HttpResponse) -> D
) : D {
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
