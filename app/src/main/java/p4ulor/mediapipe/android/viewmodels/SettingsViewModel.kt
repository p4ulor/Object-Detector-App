package p4ulor.mediapipe.android.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.flow
import org.koin.android.annotation.KoinViewModel
import p4ulor.mediapipe.android.utils.NetworkObserver
import p4ulor.mediapipe.android.utils.launch
import p4ulor.mediapipe.data.storage.preferences.UserPreferences
import p4ulor.mediapipe.data.storage.preferences.UserSecretPreferences
import p4ulor.mediapipe.data.storage.preferences.dataStore
import p4ulor.mediapipe.data.storage.preferences.secretDataStore
import p4ulor.mediapipe.ui.screens.settings.SettingsScreen

/**
 * This is mostly an utility view model to not bloat the [SettingsScreen]
 * All params are injected with Koin
 */
@SuppressLint("StaticFieldLeak") // Property ctx will be injected
@KoinViewModel
class SettingsViewModel(private val ctx: Context, val network: NetworkObserver) : ViewModel() {

    fun getUserSecretPrefs() = flow {
        val prefs = UserSecretPreferences.getFrom(ctx.secretDataStore)
        emit(prefs)
    }

    fun saveUserSecretPrefs(prefs: UserSecretPreferences) {
        launch {
            prefs.saveIn(ctx.secretDataStore)
        }
    }

    fun getUserPrefs() = flow {
        val prefs = UserPreferences.getFrom(ctx.dataStore)
        emit(prefs)
    }

    fun saveUserPrefs(prefs: UserPreferences) {
        launch {
            prefs.saveIn(ctx.dataStore)
        }
    }
}
