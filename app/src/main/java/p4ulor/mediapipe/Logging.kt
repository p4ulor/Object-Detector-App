package p4ulor.mediapipe

import android.util.Log
import p4ulor.mediapipe.BuildConfig

private val versionName = BuildConfig.VERSION_NAME
val TAG = "p4ulor"
val VERSION = "objdetect($versionName)"

fun i(s: String) { Log.i(TAG, s) }
inline fun <reified T> T.i(s: String) { Log.i("$TAG ${T::class.simpleName}", "$VERSION - $s") }

fun e(s: String) { Log.e(TAG, s) }
inline fun <reified T> T.e(s: String) { Log.e("$TAG ${T::class.simpleName}", "$VERSION - $s") }
