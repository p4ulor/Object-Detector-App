package p4ulor.obj.detector.data.utils

import androidx.camera.core.ImageAnalysis
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Single threaded executors are used so no internal concurrency is used unlike using
 * a thread pool. There's a common executor for lightweight operations and a dedicated executor
 * for image analysis, that's used in [ImageAnalysis.setAnalyzer]
 */
val executorCommon: ExecutorService = Executors.newSingleThreadExecutor()
val executorForImgAnalysis: ExecutorService = Executors.newSingleThreadExecutor()
