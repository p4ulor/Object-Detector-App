package p4ulor.mediapipe.data.sources.local.database.achievements

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Layer that defines the CRUD operations for [AchievementsTuple]
 * I want all reads and writes to be one-shot, so suspend functions are used
 * - https://developer.android.com/training/data-storage/room/async-queries#options
 * "Room uses its own dispatcher to run queries on a background thread. Your code should not use
 * withContext(Dispatchers.IO) to call suspending room queries. It will complicate the code and make
 * your queries run slower." says an Engineer from Google (even though it's not in the docs ...)
 * - https://medium.com/androiddevelopers/coroutines-on-android-part-iii-real-work-2ba8a2ec2f45
 */
@Dao
interface AchievementsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AchievementsTuple)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AchievementsTuple>)

    @Query("SELECT * FROM $tableAchivements WHERE detectionDate IS NULL LIMIT :limit")
    suspend fun getAllUnreachedAchievements(limit: Short = Short.MAX_VALUE) : List<AchievementsTuple>

    /** Gets all [AchievementsTuple] ordered by [AchievementsTuple.objectName] */
    @Query("SELECT * FROM $tableAchivements ORDER BY objectName ASC LIMIT :limit")
    suspend fun getAll(limit: Short = Short.MAX_VALUE) : List<AchievementsTuple>

    @Query("SELECT * FROM $tableAchivements WHERE objectName=:objectName")
    suspend fun get(objectName: String): AchievementsTuple?

    @Query("UPDATE $tableAchivements SET detectionDate=NULL")
    suspend fun resetAll()
}
