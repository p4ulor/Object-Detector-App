package p4ulor.mediapipe.data.sources.local.storage.achievements

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Layer that defines the CRUD operations for [AchievementsTableTuple]
 * Since all reads and writes are one-shot, suspend functions are used
 * - https://developer.android.com/training/data-storage/room/async-queries#options
 */
@Dao
interface AchievementsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: AchievementsTableTuple)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AchievementsTableTuple>)

    @Query("SELECT * FROM $tableAchivements LIMIT :limit")
    suspend fun getAll(limit: Short = Short.MAX_VALUE) : List<AchievementsTableTuple>

    @Query("SELECT * FROM $tableAchivements WHERE objectName=:objectName")
    suspend fun getItem(objectName: String): AchievementsTableTuple?
}
