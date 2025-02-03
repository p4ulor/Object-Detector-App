package p4ulor.mediapipe.android.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.koin.android.annotation.KoinViewModel
import p4ulor.mediapipe.android.utils.launch
import p4ulor.mediapipe.android.utils.toStateFlow
import p4ulor.mediapipe.data.storage.UserPreferences
import p4ulor.mediapipe.data.storage.UserSecretPreferences
import p4ulor.mediapipe.data.storage.dataStore
import p4ulor.mediapipe.data.storage.secretDataStore
import p4ulor.mediapipe.ui.screens.settings.SettingsScreen

/**
 * Manages the acquisition and saving of user preferences. [toStateFlow] is used so that
 * the initialValue is set here, to not bloat the [SettingsScreen]
 */
@SuppressLint("StaticFieldLeak") // Property ctx will be injected
@KoinViewModel
class SettingsViewModel(private val ctx: Context) : ViewModel(){

    fun getUserSecretPrefs() = flow {
        val prefs = UserSecretPreferences.getFrom(ctx.secretDataStore)
        emit(prefs)
    }.flowOn(Dispatchers.IO).toStateFlow(initialValue = UserSecretPreferences())

    fun saveUserSecretPrefs(prefs: UserSecretPreferences) {
        launch(Dispatchers.IO) { prefs.saveIn(ctx.secretDataStore) }
    }

    fun getUserPrefs() = flow {
        val prefs = UserPreferences.getFrom(ctx.dataStore)
        emit(prefs)
    }.flowOn(Dispatchers.IO).toStateFlow(initialValue = UserPreferences())

    fun saveUserPrefs(prefs: UserPreferences) {
        launch(Dispatchers.IO) { prefs.saveIn(ctx.dataStore) }
    }
}
