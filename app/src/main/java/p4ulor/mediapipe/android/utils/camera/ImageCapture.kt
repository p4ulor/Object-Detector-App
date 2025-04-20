package p4ulor.mediapipe.android.utils.camera

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.resolutionselector.ResolutionSelector
import p4ulor.mediapipe.data.sources.cloud.gemini.MimeType
import p4ulor.mediapipe.data.utils.encodeToBase64
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

fun ImageCapture.takePic(ctx: Context, saveInStorage: Boolean, onImageSaved: (picture: Picture) -> Unit){
    if(saveInStorage){
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
                    outputFileResults.savedUri?.let {
                        onImageSaved(Picture.File(it))
                    }
                }
            }
        )
    } else {
        takePicture(
            executorCommon,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(error: ImageCaptureException) {
                    e("Error capturing image: $error")
                }

                /** Captures image in JPEG (read [ImageCapture.OnImageCapturedCallback.onCaptureSuccess]) */
                override fun onCaptureSuccess(image: ImageProxy) {
                    val buffer = image.planes[0].buffer // JPEG only has 1 color plane
                    val bytes = ByteArray(size = buffer.remaining())
                    buffer.get(/*destination =*/ bytes)
                    image.close()

                    i("Image captured in Base64 format")
                    onImageSaved(Picture.Base64(encodeToBase64(bytes)))
                }
            }
        )
    }
}
