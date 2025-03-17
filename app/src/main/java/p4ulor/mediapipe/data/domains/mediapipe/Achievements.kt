package p4ulor.mediapipe.data.domains.mediapipe

import p4ulor.mediapipe.data.sources.local.database.achievements.AchievementsTableTuple

/**
 * @param objectName one of the strings in the [80 objects list](https://storage.googleapis.com/mediapipe-tasks/object_detector/labelmap.txt)
 */
data class Achievement(
    val objectName: String,
    val detectionDate: String? = null
)  {
    companion object {
        /** Ignores the lines in the .txt file that have "???" for some unknown reason */
        const val invalidName = "???"
        fun from(achivement: AchievementsTableTuple) = Achievement(achivement.objectName)
        fun from(achivements: List<AchievementsTableTuple>) = achivements.map { from(it) }
    }
}

data class UserAchievements(
    var userName: String? = null,
    val achievements: List<Achievement> = emptyList()
)
