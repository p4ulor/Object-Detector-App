package p4ulor.obj.detector.data.domains.firebase

import com.google.firebase.firestore.DocumentSnapshot
import p4ulor.obj.detector.e

/** Read [User] for more info about Firebase */
data class ObjectDetectionStats(
    val objectName: String = "",
    val detectionCount: Int = 0
) {
    companion object { // field names explicitly used in Firestore in case they are changed in the constructor
        const val DETECTION_COUNT = "detectionCount"

        fun from(doc: DocumentSnapshot) = ObjectDetectionStats(
            objectName = doc.id,
            detectionCount = doc.data?.get(DETECTION_COUNT).let {
                val asInt = (it as? Number)?.toInt() // support both Long and Int, even though firestore seems to default Long
                if (asInt != null) {
                    asInt
                } else {
                    e("$DETECTION_COUNT not valid, returning 0")
                    0
                }
            }
        )
    }
}
