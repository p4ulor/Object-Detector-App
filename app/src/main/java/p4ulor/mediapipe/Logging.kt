package p4ulor.mediapipe

import android.util.Log

const val TAG = "p4ulor"

fun i(s: String) = Log.i(TAG, s)
inline fun <reified T> T.i(s: String) = Log.i(T::class.simpleName, s)

fun e(s: String) = Log.e(TAG, s)
inline fun <reified T> T.e(s: String) = Log.e(T::class.simpleName, s)
