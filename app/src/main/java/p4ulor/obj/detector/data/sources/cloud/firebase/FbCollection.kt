package p4ulor.obj.detector.data.sources.cloud.firebase

import p4ulor.obj.detector.data.domains.firebase.ObjectDetectionStats
import p4ulor.obj.detector.data.domains.firebase.User

/** Collections for [User], [User], and [ObjectDetectionStats] (respectively) */
enum class FbCollection(val id: String) {
    Users("users"),
    TopUsers("top-users"),
    TopObjects("top-objects");
}
