package p4ulor.obj.detector.ui.components.utils

import android.content.Context
import android.net.Uri
import coil3.ImageLoader
import coil3.executeBlocking
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import java.util.Base64

fun Context.base64ToImageRequest(base64: String): ImageRequest {
    val base64ForCoil = base64.substringAfter("base64,") // https://stackoverflow.com/a/78151198/9375488
    val imageBytes = Base64.getDecoder().decode(base64ForCoil)
    return ImageRequest.Builder(this)
        .data(imageBytes)
        .size(Size.ORIGINAL)
        .crossfade(true)
        .build()
}

fun Context.pathToImageRequest(path: Uri) =
    ImageRequest.Builder(this)
        .data(path)
        .crossfade(true)
        .build()

/** This is useful in the case an image that was saved was deleted */
fun ImageRequest?.hasError(ctx: Context) = this?.let {
    ImageLoader(ctx).executeBlocking(this) is ErrorResult
} ?: false
