package p4ulor.mediapipe.data.domains.mediapipe

import p4ulor.mediapipe.data.sources.local.database.achievements.AchievementsTuple
import p4ulor.mediapipe.data.utils.toGlobalDateFormat

/**
 * @param objectName one of the strings in the [80 objects list](https://storage.googleapis.com/mediapipe-tasks/object_detector/labelmap.txt)
 */
data class Achievement(
    val objectName: String,
    var detectionDate: String? = null
)  {
    companion object {
        /** Ignores the lines in the .txt file that have "???" for some unknown reason */
        const val invalidName = "???"
        fun from(achivement: AchievementsTuple) = with(achivement){
            Achievement(
                objectName,
                detectionDate?.toGlobalDateFormat()
            )
        }
        fun from(achivements: List<AchievementsTuple>) = achivements.map { from(it) }
    }
}

class UserAchievements(
    var userName: String? = null,
    achievements: List<Achievement>
) {

    var achievements = achievements
        private set

    fun reset() {
        achievements = achievements.map {
            it.apply {
                detectionDate = null
            }
        }
    }
}
