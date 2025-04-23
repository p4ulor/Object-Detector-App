package p4ulor.obj.detector.data.sources.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import p4ulor.obj.detector.R
import p4ulor.obj.detector.android.utils.readFromRaw
import p4ulor.obj.detector.data.domains.mediapipe.Achievement
import p4ulor.obj.detector.data.sources.local.database.achievements.AchievementsDao
import p4ulor.obj.detector.data.sources.local.database.achievements.AchievementsTuple
import p4ulor.obj.detector.data.sources.local.database.achievements.tableAchivements

/** Defines a Room database for the application, and the Data Access Objects for it's tables */
@Database(
    entities = [
        AchievementsTuple::class
    ],
    version = 2 // increase this when addi
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun achievements(): AchievementsDao
}

/** Initialize the DB tables */
suspend fun Context.initializeDb(db: AppDatabase) {
    val achievements = db.achievements()
    if (achievements.getAll().isEmpty()) {
        val allAchievements = readFromRaw(R.raw.mediapipe_detectable_objects).mapNotNull { objectName ->
            if (objectName != Achievement.invalidName) {
                AchievementsTuple(objectName, 0f)
            } else {
                null
            }
        }
        achievements.insertAll(allAchievements)
    }
}

/** YOLO, just to try it out since I had to change it */
val MIGRATION_V1_V2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE $tableAchivements ADD COLUMN certaintyScore REAL NOT NULL DEFAULT 0.0"
        )
    }
}
