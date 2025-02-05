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
import p4ulor.mediapipe.d
import p4ulor.mediapipe.e
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class UserSecretPreferences(
    val geminiApiKey: String? = null
) {
    companion object {
        const val secretId = "gemini"

        suspend fun getFrom(storage: DataStore<UserSecretPreferences>) = withContext(Dispatchers.IO) {
            return@withContext storage.data
                .catch { e("Error reading preferences: $it") }
                .firstOrNull() ?: UserSecretPreferences()
        }
    }

    suspend fun saveIn(storage: DataStore<UserSecretPreferences>) = withContext(Dispatchers.IO) {
        storage.updateData { this@UserSecretPreferences }
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
        d("Will read encrypted ${encryptedBytes.decodeToString()}")
        val decryptedBytes = Crypto.decrypt(UserSecretPreferences.secretId, encryptedBytes)
        val decodedJsonString = decryptedBytes.decodeToString()
        d("Decrypted ${decodedJsonString}")
        return Json.decodeFromString(decodedJsonString)
    }

    override suspend fun writeTo(data: UserSecretPreferences, output: OutputStream) {
        val json = Json.encodeToString(data)
        d("Will encrypt $json")
        val bytes = json.toByteArray()
        val encryptedBytes = Crypto.encrypt(UserSecretPreferences.secretId, bytes)
        d("Encrypted ${encryptedBytes.decodeToString()}")
        withContext(Dispatchers.IO) {
            output.use { it.write(encryptedBytes) }
        }
    }
}