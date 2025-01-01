package p4ulor.mediapipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

const val TAG = "p4ulor"

fun i(s: String) = Log.i(TAG, s)
inline fun <reified T> T.i(s: String) = Log.i(T::class.simpleName, s)

fun e(s: String) = Log.e(TAG, s)
inline fun <reified T> T.e(s: String) = Log.e(T::class.simpleName, s)

/** Converts a cold [Flow] into a hot [StateFlow] in the context of a ViewModel instance */
context(ViewModel)
fun <T> Flow<T>.toStateFlow(
    initialValue: T,
    started: SharingStarted = SharingStarted.Lazily,
): StateFlow<T> = stateIn(viewModelScope, started, initialValue)
