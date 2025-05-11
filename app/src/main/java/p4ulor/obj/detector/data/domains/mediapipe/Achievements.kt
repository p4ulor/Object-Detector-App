package p4ulor.obj.detector.data.domains.mediapipe

import p4ulor.obj.detector.data.domains.firebase.User
import p4ulor.obj.detector.data.domains.firebase.UserAchievement
import p4ulor.obj.detector.data.sources.local.database.achievements.AchievementsTuple
import p4ulor.obj.detector.data.utils.round
import p4ulor.obj.detector.data.utils.toGlobalDateFormat
import p4ulor.obj.detector.data.utils.trimToDecimals

/**
 * @param objectName one of the strings in the [80 objects list](https://storage.googleapis.com/mediapipe-tasks/object_detector/labelmap.txt)
 */
data class Achievement(
    val objectName: String,
    val certaintyScore: Float,
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

/** Used for comparing the local achievements with the achievements of the user in Firestore */
fun List<Achievement>.isDifferentThan(other: List<UserAchievement>?): Boolean{
    if (other == null) return true
    if (other.isEmpty()) return true
    var areDifferent = false
    forEachIndexed { index, achievement ->
        if (!achievement.isEqualTo(other[index])) {
            areDifferent = true
            return@forEachIndexed
        }
    }

    return areDifferent
}
