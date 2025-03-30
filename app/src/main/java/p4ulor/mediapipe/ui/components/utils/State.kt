package p4ulor.mediapipe.ui.components.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * Util function that creates a remembered value and that has a function to toggle the boolean
 * state, thus avoiding doing:
 * ```kotlin
 * var state by remember { mutableStateOf(false) }
 * // ...
 * state = state.not()
 * // ...
 * state = state.not()
 * // ...
 * state = state.not()
 * ```
 */
@Composable
fun rememberToggleableState(initialValue: Boolean = false): ToggleableState<Boolean> {
    val state = remember { mutableStateOf(initialValue) }
    return remember {
        object : MutableState<Boolean> by state, ToggleableState<Boolean> {
            override var value: Boolean
                get() = state.value
                set(newValue) {
                    state.value = newValue
                }

            override fun toggle() {
                state.value = !state.value
            }
        }
    }
}

interface ToggleableState <T> : MutableState<T> {
    fun toggle()
}