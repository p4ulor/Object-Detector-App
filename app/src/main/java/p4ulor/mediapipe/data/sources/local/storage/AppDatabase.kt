package p4ulor.mediapipe.data.sources.local.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import p4ulor.mediapipe.data.sources.local.storage.achievements.AchievementsDao
import p4ulor.mediapipe.data.sources.local.storage.achievements.AchievementsTableTuple

/** Defines a RoomDatabase for the application, and it's tables */
@Database(
    entities = [
        AchievementsTableTuple::class
    ],
    version = 1
)
//@TypeConverters(::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun achivements(): AchievementsDao
}