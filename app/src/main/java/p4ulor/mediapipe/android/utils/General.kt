package p4ulor.mediapipe.android.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import p4ulor.mediapipe.android.viewmodels.MainViewModel

/** Factory class to create the MainViewModel with Application */
class MainViewModelFactory(
    private val application: Application
) : ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    constructor(compositionCtx: Context) : this(compositionCtx.applicationContext as Application)
}

/** [Same as google.accompanist](https://stackoverflow.com/a/65243835/28417805) */
fun Context.getActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}