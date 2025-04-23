package p4ulor.obj.detector

import android.util.Log
import p4ulor.obj.detector.BuildConfig

val TAG = "p4ulor"
private val versionName = BuildConfig.VERSION_NAME
val VERSION = "objdetect($versionName)"

fun i(s: String) = runIfInDebug { Log.i(TAG, s) }
inline fun <reified T> T.i(s: String) = runIfInDebug {
    Log.i("$TAG ${T::class.simpleName}", "$VERSION - $s")
}

fun e(s: String) = runIfInDebug { Log.e(TAG, s) }
inline fun <reified T> T.e(s: String) = runIfInDebug {
    Log.e("$TAG ${T::class.simpleName}", "$VERSION - $s")
}

fun d(s: String) = runIfInDebug { Log.d(TAG, s) }
inline fun <reified T> T.d(s: String) = runIfInDebug {
    Log.d("$TAG ${T::class.simpleName}", "$VERSION - $s")
}

fun runIfInDebug(block: () -> Unit){
    if (BuildConfig.DEBUG) {
        block()
    }
}
