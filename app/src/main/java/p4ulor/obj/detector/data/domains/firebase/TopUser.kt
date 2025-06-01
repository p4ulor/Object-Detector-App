package p4ulor.obj.detector.data.domains.firebase

import p4ulor.obj.detector.data.sources.cloud.firebase.FbCollection

/**
 *  A type of a document in the [FbCollection.TopUsers] collection.
 */
data class TopUser(
    val points: Int = 0
) {
    companion object { // field names explicitly used in Firestore in case they are changed in the constructor
        const val POINTS = "points"
    }
}
