package p4ulor.obj.detector.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import p4ulor.obj.detector.i

/**
 * Utility class that wraps a [DisposableEffect] and callbacks that should either cause the
 * unbinding of the camera or trigger the re-binding of the camera in order to save resources.
 * This contains the logic of the lifecycle stages where the camera should be un-binded (which is
 * when the app is "minimized", in Android terms, in [Lifecycle.Event.ON_PAUSE]) and returned on
 * [Lifecycle.Event.ON_RESUME]
 * @param [onUnbind] a callback that's called when [Lifecycle.Event.ON_PAUSE] occurs
 * @param [onBind] a callback that's called when [Lifecycle.Event.ON_RESUME] occurs
 */
@Composable
@NonRestartableComposable // Just like DisposableEffect has
fun CameraUseBinder(lifecycleOwner: LifecycleOwner, onUnbind: () -> Unit, onBind: () -> Unit){
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            if(event == Lifecycle.Event.ON_PAUSE){
                onUnbind()
            }
            if(event == Lifecycle.Event.ON_RESUME){
                onBind()
            }
        }

        lifecycle.addObserver(observer)
        onDispose {
            i("onDispose")
            lifecycle.removeObserver(observer)
        }
    }
}