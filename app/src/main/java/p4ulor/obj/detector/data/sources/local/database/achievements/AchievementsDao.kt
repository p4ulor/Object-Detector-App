package p4ulor.obj.detector.data.sources.local.database.achievements

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Layer that defines the CRUD operations for [AchievementsTuple]
 * I want all reads and writes to be one-shot, so suspend functions are used
 * - https://developer.android.com/training/data-storage/room/async-queries#options
 *
 * "Room uses its own dispatcher to run queries on a background thread. Your code should not use
 * withContext(Dispatchers.IO) to call suspending room queries. It will complicate the code and make
 * your queries run slower." says an Engineer from Google (even though it's not in the docs ...)
 * - https://medium.com/androiddevelopers/coroutines-on-android-part-iii-real-work-2ba8a2ec2f45
 *
 * PS: After reading some comments on this article and reflecting on it, basically I don't have to
 * necessarily only use Dispatchers.IO. Technically, it's not a concrete mistake to use Dispatchers.IO
 */
@Dao
interface AchievementsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AchievementsTuple)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AchievementsTuple>)

    @Query("SELECT * FROM $tableAchivements WHERE detectionDate IS NULL LIMIT :limit")
    suspend fun getAllUnreachedAchievements(limit: Short = Short.MAX_VALUE) : List<AchievementsTuple>

    /** Gets all [AchievementsTuple] ordered by name */
    @Query("SELECT * FROM $tableAchivements ORDER BY objectName ASC LIMIT :limit")
    suspend fun getAllOrderedByName(limit: Short = Short.MAX_VALUE) : List<AchievementsTuple>

    /** Gets all [AchievementsTuple] ordered by completion status and then by name */
    @Query(
        """
    SELECT * FROM $tableAchivements
    ORDER BY 
            CASE WHEN detectionDate IS NOT NULL THEN 0 ELSE 1 END ASC,
            objectName ASC
    LIMIT :limit
    """
    )
    suspend fun getAllOrderedByCompletionAndName(limit: Short = Short.MAX_VALUE): List<AchievementsTuple>

    @Query("SELECT * FROM $tableAchivements WHERE objectName=:objectName")
    suspend fun get(objectName: String): AchievementsTuple?

    @Query("UPDATE $tableAchivements SET detectionDate=NULL, certaintyScore=0")
    suspend fun resetAll()
}
