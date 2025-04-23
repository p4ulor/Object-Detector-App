package p4ulor.obj.detector.android.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.flow
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Single
import p4ulor.obj.detector.android.utils.NetworkObserver
import p4ulor.obj.detector.android.viewmodels.utils.launch
import p4ulor.obj.detector.data.sources.local.preferences.UserPreferences
import p4ulor.obj.detector.data.sources.local.preferences.UserSecretPreferences
import p4ulor.obj.detector.data.sources.local.preferences.dataStore
import p4ulor.obj.detector.data.sources.local.preferences.secretDataStore
import p4ulor.obj.detector.ui.screens.settings.SettingsScreen

/**
 * This is mostly an utility view model to not bloat the [SettingsScreen]
 * All params are injected automatically with Koin
 */
@SuppressLint("StaticFieldLeak") // Property ctx will be injected
@Single // So it's also not re-instantiated on composable destruction's
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
