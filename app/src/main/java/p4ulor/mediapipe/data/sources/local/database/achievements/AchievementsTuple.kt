package p4ulor.mediapipe.data.sources.local.database.achievements

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

const val tableAchivements = "achievements"

@Entity(tableName = tableAchivements)
data class AchievementsTuple(
    @PrimaryKey
    val objectName: String,
    @ColumnInfo(defaultValue = "0.0")
    val certaintyScore: Float,
    val detectionDate: Date? = null
)
