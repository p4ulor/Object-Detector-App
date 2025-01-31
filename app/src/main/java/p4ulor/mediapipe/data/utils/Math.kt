package p4ulor.mediapipe.data.utils

import java.math.RoundingMode
import java.text.DecimalFormat
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
 * Formats a float to a string up to a specified number of decimal places,
 * and the last digit is repeated up to that number of decimal places
 */
fun Float.toStringUpTo(decimals: Int): String {
    val parts = this.toString().split(".")
    val decimalPart = parts[1]
    val lastDigit = decimalPart.lastOrNull() ?: '0'
    val repeatedDecimals = decimalPart.padEnd(decimals, lastDigit)
    return "${parts[0]}.$repeatedDecimals"
}