package p4ulor.mediapipe.data.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Base64
import java.util.Date
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val executor: ExecutorService = Executors.newSingleThreadExecutor()

fun getTodaysDate(): String {
    val date = Date.from(Instant.now())
    val formatter = SimpleDateFormat("dd/MM/yy")
    return formatter.format(date)
}

fun encodeToBase64(bytes: ByteArray): String =
    Base64.getEncoder().withoutPadding().encodeToString(bytes)