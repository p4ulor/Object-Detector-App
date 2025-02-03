package p4ulor.mediapipe.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import p4ulor.mediapipe.data.domains.mediapipe.Model
import p4ulor.mediapipe.e

data class UserPreferences(
    val minDetectCertainty: Float,
    val maxObjectsDetections: Int,
    val enableAnimations: Boolean,
    val selectedModel: String
) {
    companion object {
        object Key {
            val minDetectCertainty = floatPreferencesKey("minDetectCertainty")
            val maxObjectsDetections = intPreferencesKey("enableAnimations")
            val enableAnimations = booleanPreferencesKey("enableAnimations")
            val selectedModel = stringPreferencesKey("selectedModel")
        }

        object Default {
            val minDetectCertainty = 50f
            val maxObjectsDetections = 3
            val enableAnimations = true
            val selectedModel = Model.EFFICIENTDETV0.name
        }

        suspend fun getFrom(storage: DataStore<Preferences>) : UserPreferences? {
            return storage.data
                .catch {
                    e("Error reading preferences: $it")
                }
                .firstOrNull()?.run {
                    UserPreferences(
                        this[Key.minDetectCertainty] ?: Default.minDetectCertainty,
                        this[Key.maxObjectsDetections] ?: Default.maxObjectsDetections,
                        this[Key.enableAnimations] ?: Default.enableAnimations,
                        this[Key.selectedModel] ?: Default.selectedModel,
                    )
                }
        }
    }

    suspend fun saveIn(storage: DataStore<Preferences>) {
        storage.edit { preferences ->
            preferences[Key.minDetectCertainty] = this.minDetectCertainty
            preferences[Key.maxObjectsDetections] = this.maxObjectsDetections
            preferences[Key.enableAnimations] = this.enableAnimations
            preferences[Key.selectedModel] = this.selectedModel
        }
    }
}