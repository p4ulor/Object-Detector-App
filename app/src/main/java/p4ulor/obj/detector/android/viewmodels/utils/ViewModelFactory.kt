package p4ulor.obj.detector.android.viewmodels.utils

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/** Factory method to create any [AndroidViewModel] with Application */
inline fun <reified VM : ViewModel> create(
    context: Context
) = object : ViewModelProvider.AndroidViewModelFactory(context as Application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VM::class.java)) {
            return VM::class.java.constructors.first().newInstance(context as Application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
