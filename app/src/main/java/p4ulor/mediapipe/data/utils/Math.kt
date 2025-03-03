package p4ulor.mediapipe.data.utils

import kotlin.math.pow

fun Float.round(decimals: Int): Float {
    var multiplier = 1.0f
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}

fun Float.trimToDecimals(decimals: Int): Float {
    val multiplier = 10.0.pow(decimals).toFloat()
    return (this * multiplier).toInt() / multiplier
}
