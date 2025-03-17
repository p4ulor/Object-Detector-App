package p4ulor.mediapipe.data.sources.local.database.achievements

import androidx.room.Entity
import androidx.room.PrimaryKey
const val tableAchivements = "achievements"

@Entity(tableName = tableAchivements)
data class AchievementsTableTuple(
    @PrimaryKey
    val objectName: String,
    val detectionDate: String? = null
)
