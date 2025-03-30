package p4ulor.mediapipe.data.sources.local.database.achievements

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

const val tableAchivements = "achievements"

@Entity(tableName = tableAchivements)
data class AchievementsTuple(
    @PrimaryKey
    val objectName: String,
    val detectionDate: Date? = null
)
