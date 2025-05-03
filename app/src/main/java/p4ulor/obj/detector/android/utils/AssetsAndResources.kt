package p4ulor.obj.detector.android.utils

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import java.io.BufferedReader
import java.io.InputStreamReader

fun Context.doesAssetExist(name: String) = runCatching {
    assets.open(name).close()
}.isSuccess

/** Returns the lines of the file */
fun Context.readFromRaw(@RawRes resId: Int): List<String> {
    val inputStream = resources.openRawResource(resId)
    val reader = BufferedReader(InputStreamReader(inputStream))

    return reader.use {
        it.readLines()
    }
}

fun Context.getBitmapFor(@DrawableRes resource: Int): Bitmap? = runCatching {
    val uri = resources.toUri(resource)
    val inputStream = contentResolver.openInputStream(uri!!)
    BitmapFactory.decodeStream(inputStream).also {
        inputStream?.close()
    }
}.getOrNull()

/**
 * Returns an Uri like `android.resource://p4ulor.obj.detector/mipmap/app_icon_round`
 * (meant for drawables)
 */
fun Resources.toUri(@DrawableRes resource: Int): Uri {
    return Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(this.getResourcePackageName(resource))
        .appendPath(this.getResourceTypeName(resource))
        .appendPath(this.getResourceEntryName(resource))
        .build()
}

fun Context.getResourcePath(@RawRes resource: Int) = "android.resource://${packageName}/$resource"
