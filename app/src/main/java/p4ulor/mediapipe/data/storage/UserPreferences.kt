package p4ulor.mediapipe.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import p4ulor.mediapipe.data.domains.mediapipe.Models
import p4ulor.mediapipe.data.domains.mediapipe.ObjectDetectorSettings
import p4ulor.mediapipe.e

data class UserPreferences(
    var minDetectCertainty: Float = Default.minDetectCertainty,
    var maxObjectDetections: Int = Default.maxObjectsDetections,
    var enableAnimations: Boolean = Default.enableAnimations,
    var selectedModel: String = Default.selectedModel
) {
    companion object {
        // Don't put this inside an object like the other things, or these will be null
        val minDetectCertaintyKey = floatPreferencesKey("minDetectCertainty")
        val maxObjectDetectionsKey = intPreferencesKey("maxObjectDetections")
        val enableAnimationsKey = booleanPreferencesKey("enableAnimations")
        val selectedModelKey = stringPreferencesKey("selectedModel")

        object Default {
            const val minDetectCertainty = 0.50f
            const val maxObjectsDetections = 3
            const val enableAnimations = false
            val selectedModel = Models.EFFICIENTDETV0.name
        }

        object Ranges {
            val detectionCertainty = ObjectDetectorSettings.detectionCertaintyRange
            val objectDetections = 1..ObjectDetectorSettings.maxObjectDetections
            val models = Models.values().map { it.name }
        }

        suspend fun getFrom(storage: DataStore<Preferences>) = withContext(Dispatchers.IO) {
            return@withContext storage.data
                .catch {
                    e("Error reading preferences: $it")
                }
                .firstOrNull().run {
                    UserPreferences(
                        this?.get(minDetectCertaintyKey) ?: Default.minDetectCertainty,
                        this?.get(maxObjectDetectionsKey) ?: Default.maxObjectsDetections,
                        this?.get(enableAnimationsKey) ?: Default.enableAnimations,
                        this?.get(selectedModelKey) ?: Default.selectedModel,
                    )
                }
        }
    }

    suspend fun saveIn(storage: DataStore<Preferences>) = withContext(Dispatchers.IO) {
        storage.edit { preferences ->
            preferences[minDetectCertaintyKey] = this@UserPreferences.minDetectCertainty
            preferences[maxObjectDetectionsKey] = this@UserPreferences.maxObjectDetections
            preferences[enableAnimationsKey] = this@UserPreferences.enableAnimations
            preferences[selectedModelKey] = this@UserPreferences.selectedModel
        }
    }
}