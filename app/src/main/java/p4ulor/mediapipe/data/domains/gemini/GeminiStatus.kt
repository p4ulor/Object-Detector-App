package p4ulor.mediapipe.data.domains.gemini

/**
 * Defines the states the user can set for the displaying the Gemini chat ([ON] or [OFF]) or
 * [DISCONNECTED], which should be set after the device lost connection
 */
enum class GeminiStatus {
    ON,
    OFF,
    DISCONNECTED;

    fun toggle() = when(this) {
        ON -> OFF
        OFF -> ON
        else -> ON
    }

    val isEnabled get() = this == ON
    val isDisconnected get() = this == DISCONNECTED
}