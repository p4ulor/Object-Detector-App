package p4ulor.mediapipe.android.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.resolutionselector.ResolutionSelector
import p4ulor.mediapipe.data.sources.gemini.MimeType
import p4ulor.mediapipe.data.utils.executorCommon
import p4ulor.mediapipe.e
import p4ulor.mediapipe.i

object ImageCaptureDefault {
    val mimeType = MimeType.JPEG
    const val picturesFolder = "Pictures/Object-Detector"
}

fun createImageCaptureUseCase(cameraPreviewRatio: ResolutionSelector) = ImageCapture.Builder()
    .setTargetAspectRatio(cameraPreviewRatio.toInt())
    .setFlashMode(ImageCapture.FLASH_MODE_OFF) // User has to manually turn on flash
    .build()

fun ImageCapture.takePic(ctx: Context, onImageSaved: (picture: Picture) -> Unit){
    val fileName = "${System.currentTimeMillis()}.jpg"

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, ImageCaptureDefault.mimeType.value)
        put(MediaStore.Images.Media.RELATIVE_PATH, ImageCaptureDefault.picturesFolder)
    }

    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
        ctx.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    takePicture(
        outputFileOptions,
        executorCommon,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(error: ImageCaptureException) {
                e("Error capturing image: $error")
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                i("Image saved at: ${outputFileResults.savedUri}")
                onImageSaved(Picture(outputFileResults.savedUri!!))
            }
        }
    )
}

data class Picture(
    val path: Uri,
    val mimeType: MimeType = ImageCaptureDefault.mimeType
)