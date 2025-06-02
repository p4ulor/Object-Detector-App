package p4ulor.obj.detector.data.domains.mediapipe

import p4ulor.obj.detector.data.domains.firebase.UserAchievement
import p4ulor.obj.detector.data.domains.firebase.calculatePoints
import p4ulor.obj.detector.data.sources.local.database.achievements.AchievementsTuple
import p4ulor.obj.detector.data.utils.toGlobalDateFormat
import p4ulor.obj.detector.data.utils.trimToDecimals

/**
 * @param objectName one of the strings in the [80 objects list](https://storage.googleapis.com/mediapipe-tasks/object_detector/labelmap.txt)
 */
data class Achievement(
    val objectName: String,
    var certaintyScore: Float,
    var detectionDate: String? = null
) {

    fun isEqualTo(other: UserAchievement): Boolean {
        return this.objectName == other.objectName &&
                this.certaintyScore == other.certaintyScore
    }

    companion object {
        /** Ignores the lines in the .txt file that have "???" for some unknown reason */
        const val invalidName = "???"

        fun from(achivement: AchievementsTuple) = with(achivement){
            Achievement(
                objectName,
                certaintyScore,
                detectionDate?.toGlobalDateFormat()
            )
        }

        fun from(achivements: List<AchievementsTuple>) = achivements.map { from(it) }
    }
}

fun List<Achievement>.reset() = toMutableList().map {
    it.apply {
        detectionDate = null
        certaintyScore = 0f
    }
}

fun List<Achievement>.getDonePercentage() = count {
    it.detectionDate != null
} / this.size.toFloat()

fun List<Achievement>.calculatePoints() = fold(initial = 0f) { sum, achiv ->
    sum + achiv.certaintyScore
}.trimToDecimals(decimals = 2)

fun List<Achievement>.toUserAchievements() = map {
    UserAchievement(it.objectName, it.certaintyScore)
}.toList()

/**
 * Gets the diff between the local achievements [this] with the [other] in Firestore
 * @return the points trimmed by 2 decimals
 */
fun List<Achievement>.pointsDifferenceBetween(other: List<UserAchievement>?): Float {
    val localAchievements = this.calculatePoints()
    val submittedAchievements = other.orEmpty().calculatePoints()
    return (localAchievements - submittedAchievements).trimToDecimals(decimals = 2) // Could be negative if the user deleted his local achievements
}
