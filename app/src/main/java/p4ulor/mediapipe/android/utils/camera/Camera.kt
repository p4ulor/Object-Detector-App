package p4ulor.mediapipe.android.utils.camera

import android.content.Context
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.DynamicRange
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.ui.geometry.Size
import p4ulor.mediapipe.data.utils.executorCommon
import p4ulor.mediapipe.e
import p4ulor.mediapipe.i
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Util function to get a camera provider, specially since the `get()` operation is blocking and can
 * throw exception (although it might be very rare)
 * [suspendCoroutine] is used to treat this blocking and synchronous `get()` call to an asynchronous
 * call that can be used with coroutines
 */
suspend fun Context.getCameraProvider(): ProcessCameraProvider? = suspendCoroutine {
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            val camera = runCatching { cameraProvider.get() }.getOrNull()
            it.resume(camera)
        }, executorCommon)
    }
}

/**
 * This function is used to calculate the size of a Box (our camera preview) after scaling it
 * down to be fitted in a Container while preserving the aspect ration of the Box
 *
 * To fit the Box in the Container, we consider the aspect ratio (AR) cases:
 * 1. Box AR is wider than Container:
 *      1. Set Box width equal to Container width
 *      2. Scale down height of Box and keep Box AR
 * 2. Box AR is taller than Container:
 *      1. Set Box height equal to Container height
 *      2. Scale down width of Box and keep Box AR
 */
fun getSizeOfBoxKeepingRatioGivenContainer(container: Size, box: Size) : Size {
    val boxRatio = box.width / box.height
    val containerRatio = container.width / container.height

    return if (boxRatio > containerRatio) {
        Size(
            width = container.width,
            height =  (container.width / box.width) * box.height,
        )
    } else {
        Size(
            width = (container.height / box.height) * box.width,
            height = container.height
        )
    }
}

fun ResolutionSelector.toSize() = when(this.aspectRatioStrategy){
    AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY -> Size(height = 4f, width = 3f)
    AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY -> Size(height = 16f, width = 9f)
    else -> Size(height = 16f, width = 9f).also { e("Unhandled case") }
}

fun ResolutionSelector.toInt() = when(this.aspectRatioStrategy){
    AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY -> AspectRatio.RATIO_4_3
    AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY -> AspectRatio.RATIO_16_9
    else -> AspectRatio.RATIO_16_9.also { e("Unhandled case") }
}

object CameraConstants {
    val RATIO_16_9 = ResolutionSelector.Builder().setAspectRatioStrategy(
        AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY
    ).build()

    val RATIO_4_3 = ResolutionSelector.Builder().setAspectRatioStrategy(
        AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
    ).build()

    fun ResolutionSelector.toggle() = when(aspectRatioStrategy){
        AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY -> RATIO_16_9
        AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY -> RATIO_4_3
        else -> { e("Unhandled case"); RATIO_16_9 }
    }
}

val ResolutionSelector.is4by3: Boolean
    get() = this == CameraConstants.RATIO_4_3

/** OpenGL pipeline supported dynamic range format */
private val openGLPipelineSupportedDynamicRanges = setOf(
    DynamicRange.SDR,
    DynamicRange.HLG_10_BIT
)

/**
 * @return true if the back camera supports HDR (High Dynamic Range)
 * HLG10 (Hybrid log–gamma 10 Bit) is the baseline HDR standard that device makers must support on
 * cameras with 10-bit output
 * - https://android-developers.googleblog.com/2024/12/whats-new-in-camerax-140-and-jetpack-compose-support.html
 */
val ProcessCameraProvider.isHdrSupported: DynamicRange?
    get() = run {
        val range = DynamicRange.HDR10_10_BIT
        val supports = getCameraInfo(CameraSelector.DEFAULT_BACK_CAMERA)
                .querySupportedDynamicRanges(openGLPipelineSupportedDynamicRanges)
                .contains(range)
        if(supports) {
            i("Supports $range")
            range
        } else {
            i("Does not support $range")
            null
        }
    }

val Camera?.hasFlash: Boolean
    get() = this?.cameraInfo?.hasFlashUnit() == true

fun Camera.toggleFlash(isFlashEnabled: Boolean) {
    cameraControl.enableTorch(isFlashEnabled).addListener( {
        i("Flashlight updated")
    }, executorCommon)
}