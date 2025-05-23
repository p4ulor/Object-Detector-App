package p4ulor.obj.detector.android.viewmodels.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

/** Converts a cold [Flow] into a hot [StateFlow] in the context of a ViewModel */
context(ViewModel)
fun <T> Flow<T>.toStateFlow(
    initialValue: T,
    started: SharingStarted = SharingStarted.Lazily,
): StateFlow<T> = stateIn(viewModelScope, started, initialValue)

/**
 * - Launches a coroutine using the [viewModelScope] in the context of a ViewModel.
 * - Uses [Dispatchers.Default] by default.
 * - Note: using [EmptyCoroutineContext] (the default context param of [CoroutineScope.launch]) will
 * fallback to using [Dispatchers.Main.immediate] in Android. See [viewModelScope] for more info
 */
fun ViewModel.launch(
    useMainDispatcher: Boolean = false,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = viewModelScope.launch(
    context = if(useMainDispatcher) Dispatchers.Main.immediate else Dispatchers.Default,
    start = start,
    block = block
)

/**
 * - Launches a coroutine using the [viewModelScope] in the context of a ViewModel.
 * - Uses [Dispatchers.Default] by default.
 * - Note: using [EmptyCoroutineContext] (the default context param of [CoroutineScope.launch]) will
 * fallback to using [Dispatchers.Main.immediate] in Android. See [viewModelScope] for more info
 */
fun <T> ViewModel.async(
    useMainDispatcher: Boolean = false,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> = viewModelScope.async(
    context = if(useMainDispatcher) Dispatchers.Main.immediate else Dispatchers.Default,
    start = start,
    block = block
)
