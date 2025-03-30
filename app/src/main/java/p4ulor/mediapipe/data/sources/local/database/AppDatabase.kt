package p4ulor.mediapipe.data.sources.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import p4ulor.mediapipe.data.sources.local.database.achievements.AchievementsDao
import p4ulor.mediapipe.data.sources.local.database.achievements.AchievementsTuple

/** Defines a Room database for the application, and the Data Access Objects for it's tables */
@Database(
    entities = [
        AchievementsTuple::class
    ],
    version = 1
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun achievements(): AchievementsDao
}
