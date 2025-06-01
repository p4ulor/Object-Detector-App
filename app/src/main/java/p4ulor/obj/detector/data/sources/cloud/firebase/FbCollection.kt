package p4ulor.obj.detector.data.sources.cloud.firebase

import p4ulor.obj.detector.data.domains.firebase.ObjectDetectionStats
import p4ulor.obj.detector.data.domains.firebase.User
import p4ulor.obj.detector.data.domains.firebase.TopUser
import p4ulor.obj.detector.ui.screens.achievements.leaderboard.DonutChart

/** Collections for [User], [TopUser], and [ObjectDetectionStats] (respectively) */
enum class FbCollection(val id: String, val desiredDocsCap: Int = Int.MAX_VALUE) {
    Users("users"),
    TopUsers("top-users", desiredDocsCap = 5),
    TopObjects("top-objects", desiredDocsCap = DonutChart.MAX_ENTITIES);
}
