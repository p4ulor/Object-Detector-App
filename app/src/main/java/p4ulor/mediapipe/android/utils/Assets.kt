package p4ulor.mediapipe.android.utils

import android.content.Context

fun Context.doesAssetExist(name: String) = runCatching {
    assets.open(name).close()
}.isSuccess
