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

/**
 * Formats a float to a string and extends the decimals up the specified number of
 * decimal places by it with 0
 */
fun Float.toStringUpTo(decimals: Int): String {
    val parts = this.toString().split(".")
    val decimalPart = parts[1]
    val repeatedDecimals = decimalPart.padEnd(decimals, '0')
    return "${parts[0]}.$repeatedDecimals"
}