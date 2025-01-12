package p4ulor.mediapipe.android.utils

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import p4ulor.mediapipe.android.viewmodels.MainViewModel
import kotlin.reflect.KClass

/**
 * Factory method to create any [ViewModel] with Application
 */
inline fun create(
    viewModel: KClass<*>,
    context: Context
) = object : ViewModelProvider.AndroidViewModelFactory(context as Application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(viewModel.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(context as Application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
