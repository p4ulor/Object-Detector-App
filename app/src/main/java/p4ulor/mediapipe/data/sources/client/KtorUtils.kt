package p4ulor.mediapipe.data.sources.client

import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.isSuccess

val HttpResponse.getIfOkOrNull
    get() = if (status.isSuccess()) this else null

fun HeadersBuilder.addAll(headers: Headers){
    headers.forEach { key, values ->
        appendAll(key, values)
    }
}