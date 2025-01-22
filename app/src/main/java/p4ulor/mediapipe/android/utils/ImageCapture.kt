package p4ulor.mediapipe.android.utils

import android.content.ContentValues
import android.content.Context
import android.location.Location
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.resolutionselector.ResolutionSelector
import p4ulor.mediapipe.e
import p4ulor.mediapipe.i
import java.util.concurrent.Executors

fun createImageCaptureUseCase(cameraPreviewRatio: ResolutionSelector) = ImageCapture.Builder()
    .setTargetAspectRatio(cameraPreviewRatio.toInt())
    .setFlashMode(ImageCapture.FLASH_MODE_OFF) // User has to manually turn on flash
    .build()

fun ImageCapture.takePic(
    ctx: Context,
    onImageSaved: (outputFileResults: ImageCapture.OutputFileResults) -> Unit
){
    val fileName = "${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
    }

    val metadata = ImageCapture.Metadata()
    metadata.location = Location("World")

    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
        ctx.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).setMetadata(metadata).build()

    takePicture(
        outputFileOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(error: ImageCaptureException) {
                e("Error capturing image: $error")
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                i("Image saved successfully at: ${outputFileResults.savedUri}")
                onImageSaved(outputFileResults)
            }
        }
    )
}

