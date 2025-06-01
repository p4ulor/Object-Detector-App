package p4ulor.obj.detector

import android.util.Log

val TAG = "p4ulor"
private val versionName = BuildConfig.VERSION_NAME
val VERSION = "obj.detector($versionName)"

inline fun <reified T> T.i(s: String) = runIfInDebug {
    Log.i("$TAG ${T::class.simpleName}", "$VERSION - $s")
}

inline fun <reified T> T.e(s: String) = runIfInDebug {
    Log.e("$TAG ${T::class.simpleName}", "$VERSION - $s")
}

inline fun <reified T> T.d(s: String) = runIfInDebug {
    Log.d("$TAG ${T::class.simpleName}", "$VERSION - $s")
}

fun i(s: String) = runIfInDebug { Log.i(TAG, s) }
fun e(s: String) = runIfInDebug { Log.e(TAG, s) }
fun d(s: String) = runIfInDebug { Log.d(TAG, s) }

fun runIfInDebug(block: () -> Unit){
    if (BuildConfig.DEBUG) {
        block()
    }
}
