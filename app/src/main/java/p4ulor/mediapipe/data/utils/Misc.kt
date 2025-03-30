package p4ulor.mediapipe.data.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/** 2 separate executors are used so no internal concurrency is used unlike using a thread pool */
val executorCommon: ExecutorService = Executors.newSingleThreadExecutor()
val executorForImgAnalysis: ExecutorService = Executors.newSingleThreadExecutor()

val globalDateFormat = SimpleDateFormat("dd/MM/yy")

fun getTodaysDate(): String {
    val date = Date.from(Instant.now())
    return globalDateFormat.format(date)
}

fun Date.toGlobalDateFormat(): String = globalDateFormat.format(this)
