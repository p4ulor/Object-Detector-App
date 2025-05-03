package p4ulor.obj.detector.data.domains.firebase

import com.google.firebase.auth.FirebaseUser
import p4ulor.obj.detector.data.sources.cloud.firebase.FbCollection

/**
 * A type of a document in the [FbCollection.Users] collection
 * Note: there's no need to convert it to a a hashmap when adding it to a collection
 * (since Firestore SDK for Kotlin Extensions)
 * Note: default params are defined in order for deserialization to work with Firebase
 * (at doc.toObject<User>())
 * - https://firebase.google.com/docs/firestore/manage-data/add-data#custom_objects
 */
data class User(
    val name: String = "",
    val uuid: String  = "",
    val points: Float = 0f,
    val achievementCount: Int = 0
) {

    companion object {
        fun createFrom(user: FirebaseUser) = User(
            name = user.displayName.toString(),
            uuid = user.uid,
            points = 0f,
            achievementCount = 0
        )
    }
}
