package p4ulor.obj.detector.ui.animations

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HueShiftLooper(
    private val updateInterval: Long = 50L,
    private val stepSize: Float = 3f,
    private val minHue: Float = 0f,
    private val maxHue: Float = 360f
) {
    private val _hueShift = MutableStateFlow(0f)
    val hueShift = _hueShift.asStateFlow()

    private var isIncrementing = true

    suspend fun start() {
        while (true) {
            var newHue = _hueShift.value
            newHue += if (isIncrementing) stepSize else -stepSize

            if(newHue > maxHue) {
                _hueShift.value = maxHue
                isIncrementing = false
            } else if(newHue < minHue) {
                _hueShift.value = minHue
                isIncrementing = true
            } else {
                _hueShift.value = newHue
            }

            delay(updateInterval)
        }
    }
}