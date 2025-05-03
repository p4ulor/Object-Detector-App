package p4ulor.obj.detector.data.domains.gemini

/**
 * Defines the states the user can set for the displaying the Gemini chat ([On] or [Off]) or
 * [Disconnected], which should be set after the device lost connection
 */
enum class GeminiStatus {
    On,
    Off,
    Disconnected;

    fun toggle() = when(this) {
        On -> Off
        Off -> On
        else -> On
    }

    val isEnabled get() = this == On
    val isDisconnected get() = this == Disconnected
}