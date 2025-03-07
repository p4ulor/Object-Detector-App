package p4ulor.mediapipe.android.utils

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.DrawableRes

fun Context.doesAssetExist(name: String) = runCatching {
    assets.open(name).close()
}.isSuccess

fun Context.getBitmapFor(@DrawableRes resource: Int): Bitmap? = runCatching {
    val uri = this.resources.toUri(resource)
    val inputStream = contentResolver.openInputStream(uri!!)
    BitmapFactory.decodeStream(inputStream).also {
        inputStream?.close()
    }
}.getOrNull()

private fun Resources.toUri(@DrawableRes resource: Int): Uri {
    return Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(this.getResourcePackageName(resource))
        .appendPath(this.getResourceTypeName(resource))
        .appendPath(this.getResourceEntryName(resource))
        .build()
}
