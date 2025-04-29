package p4ulor.obj.detector.data.domains.mediapipe

import p4ulor.obj.detector.data.sources.local.database.achievements.AchievementsTuple
import p4ulor.obj.detector.data.utils.toGlobalDateFormat

/**
 * @param objectName one of the strings in the [80 objects list](https://storage.googleapis.com/mediapipe-tasks/object_detector/labelmap.txt)
 */
data class Achievement(
    val objectName: String,
    val certaintyScore: Float,
    var detectionDate: String? = null
)  {

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

        fun reset(achivements: List<Achievement>) = achivements.toMutableList().map {
            it.apply {
                detectionDate = null
            }
        }

        fun getDonePercentage(achivements: List<Achievement>) = achivements.count {
            it.detectionDate != null
        } / achivements.size.toFloat()
    }
}
