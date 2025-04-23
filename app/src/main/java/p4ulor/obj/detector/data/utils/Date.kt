package p4ulor.obj.detector.data.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

fun getTodaysDate(): String {
    val date = Date.from(Instant.now())
    return globalDateFormat.format(date)
}

val globalDateFormat = SimpleDateFormat("dd/MM/yy")

fun Date.toGlobalDateFormat(): String = globalDateFormat.format(this)
