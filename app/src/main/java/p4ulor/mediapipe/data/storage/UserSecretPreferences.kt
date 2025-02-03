package p4ulor.mediapipe.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import p4ulor.mediapipe.e
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class UserSecretPreferences(
    val geminiApiKey: String? = null
) {
    companion object {
        const val secretId = "gemini"

        suspend fun getFrom(storage: DataStore<UserSecretPreferences>) : UserSecretPreferences {
            return storage.data
                .catch { e("Error reading preferences: $it") }
                .firstOrNull() ?: UserSecretPreferences()
        }
    }

    suspend fun saveIn(storage: DataStore<UserSecretPreferences>) {
        storage.updateData { this }
    }
}

/** Utility to encrypt and decrypt the key when storing it or retrieving it */
object UserSecretPreferencesSerializer: Serializer<UserSecretPreferences> {
    override val defaultValue: UserSecretPreferences
        get() = UserSecretPreferences()

    override suspend fun readFrom(input: InputStream): UserSecretPreferences {
        val encryptedBytes = withContext(Dispatchers.IO) {
            input.use { it.readBytes() }
        }
        val decryptedBytes = Crypto.decrypt(UserSecretPreferences.secretId, encryptedBytes)
        val decodedJsonString = decryptedBytes.decodeToString()
        return Json.decodeFromString(decodedJsonString)
    }

    override suspend fun writeTo(data: UserSecretPreferences, output: OutputStream) {
        val json = Json.encodeToString(data)
        val bytes = json.toByteArray()
        val encryptedBytes = Crypto.encrypt(UserSecretPreferences.secretId, bytes)
        withContext(Dispatchers.IO) {
            output.use {
                it.write(encryptedBytes)
            }
        }
    }
}