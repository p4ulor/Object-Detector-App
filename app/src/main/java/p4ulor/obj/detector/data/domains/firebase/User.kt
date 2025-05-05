package p4ulor.obj.detector.data.domains.firebase

import com.google.firebase.auth.FirebaseUser
import p4ulor.obj.detector.data.sources.cloud.firebase.FbCollection

/**
 * A type of a document in the [FbCollection.Users] collection
 * Note: there's no need to convert it to a a Hashmap when adding it to a collection
 * (since Firestore SDK for Kotlin Extensions update). This class just needs to be a data class
 * and have default params in order for implicit deserialization to work at
 * ```kotlin
 * doc.toObject<User>()
 * ```
 * Instead of default params, another constructor with empty params could be defined, but it would
 * require adding a rule in `proguard-rules.pro`in order for minify to not strip the no argument
 * constructor
 * - https://firebase.google.com/docs/firestore/manage-data/add-data#custom_objects
 */
data class User(
    val name: String = "",
    val uuid: String = "",
    val photoUri: String = "",
    val points: Float = 0f,
    val achievementCount: Int = 0
) {

    companion object {
        fun createFrom(user: FirebaseUser) = User(
            name = user.displayName.toString(),
            uuid = user.uid,
            photoUri = user.photoUrl.toString(),
            points = 0f,
            achievementCount = 0
        )
    }
}
