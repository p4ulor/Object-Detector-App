package p4ulor.mediapipe.data.sources.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import p4ulor.mediapipe.data.sources.local.database.achievements.AchievementsDao
import p4ulor.mediapipe.data.sources.local.database.achievements.AchievementsTableTuple

/** Defines a Room database for the application, and the Data Access Objects for it's tables */
@Database(
    entities = [
        AchievementsTableTuple::class
    ],
    version = 1
)
//@TypeConverters(::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun achievements(): AchievementsDao
}
