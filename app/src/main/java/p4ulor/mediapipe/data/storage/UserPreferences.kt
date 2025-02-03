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
import p4ulor.mediapipe.data.domains.mediapipe.Models
import p4ulor.mediapipe.data.domains.mediapipe.ObjectDetectorSettings
import p4ulor.mediapipe.e

data class UserPreferences(
    var minDetectCertainty: Float = Default.minDetectCertainty,
    var maxObjectsDetections: Int = Default.maxObjectsDetections,
    var enableAnimations: Boolean = Default.enableAnimations,
    var selectedModel: String = Default.selectedModel
) {
    companion object {
        object Key {
            val minDetectCertainty = floatPreferencesKey("minDetectCertainty")
            val maxObjectsDetections = intPreferencesKey("enableAnimations")
            val enableAnimations = booleanPreferencesKey("enableAnimations")
            val selectedModel = stringPreferencesKey("selectedModel")
        }

        object Default {
            val minDetectCertainty = 0.50f
            val maxObjectsDetections = 3
            val enableAnimations = true
            val selectedModel = Models.EFFICIENTDETV0.name
        }

        object Ranges {
            val detectionCertainty = ObjectDetectorSettings.detectionCertaintyRange
            val objectDetections = 1..ObjectDetectorSettings.maxObjectDetection
            val models = Models.values().map { it.name }
        }

        suspend fun getFrom(storage: DataStore<Preferences>) : UserPreferences {
            return storage.data
                .catch {
                    e("Error reading preferences: $it")
                }
                .firstOrNull().run {
                    UserPreferences(
                        this?.get(Key.minDetectCertainty) ?: Default.minDetectCertainty,
                        this?.get(Key.maxObjectsDetections) ?: Default.maxObjectsDetections,
                        this?.get(Key.enableAnimations) ?: Default.enableAnimations,
                        this?.get(Key.selectedModel) ?: Default.selectedModel,
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