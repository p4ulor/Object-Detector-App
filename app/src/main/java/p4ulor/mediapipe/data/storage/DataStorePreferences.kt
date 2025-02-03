package p4ulor.mediapipe.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/** Read docs of [dataStore] */
val Context.secretDataStore: DataStore<UserSecretPreferences> by dataStore(
    fileName = "secret-user-preferences",
    serializer = UserSecretPreferencesSerializer
)

/** Read docs of [preferencesDataStore] */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user-preferences"
)
