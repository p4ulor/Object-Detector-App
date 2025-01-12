package p4ulor.mediapipe.android.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/** Converts a cold [Flow] into a hot [StateFlow] in the context of a ViewModel */
context(ViewModel)
fun <T> Flow<T>.toStateFlow(
    initialValue: T,
    started: SharingStarted = SharingStarted.Lazily,
): StateFlow<T> = stateIn(viewModelScope, started, initialValue)

/** Launches a coroutine using the [viewModelScope] in the context of a ViewModel */
fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = viewModelScope.launch(context, start, block)
