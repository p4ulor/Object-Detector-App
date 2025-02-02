package p4ulor.mediapipe.android.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable
import p4ulor.mediapipe.android.utils.launch
import p4ulor.mediapipe.data.storage.UserSecretPreferences
import p4ulor.mediapipe.data.storage.secretDataStore

@SuppressLint("StaticFieldLeak") // Property will be injected
class SettingsViewModel(private val ctx: Context) : ViewModel(){
    fun saveGeminiKey(key: String) = with(ctx){
        launch {
            secretDataStore.updateData {
                UserSecretPreferences(key)
            }
        }
    }
}

@Serializable
data class UserPreferences(
    val geminiApiKey: String? = null
)