package p4ulor.mediapipe.data.storage

data class UserPreferences(
    val minDetectCertainty: Float,
    val maxObjectsDetections: Int,
    val enableAnimations: Boolean,
    val selectedModel: String
) {

}