package com.tracko.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tracko.app.data.local.entity.VisitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(visit: VisitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(visits: List<VisitEntity>)

    @Update
    suspend fun update(visit: VisitEntity)

    @Query("SELECT * FROM visits WHERE id = :id")
    suspend fun getById(id: Long): VisitEntity?

    @Query("SELECT * FROM visits WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<VisitEntity?>

    @Query("SELECT * FROM visits WHERE server_id = :serverId LIMIT 1")
    suspend fun getByServerId(serverId: String): VisitEntity?

    @Query("SELECT * FROM visits WHERE user_id = :userId AND planned_date = :date ORDER BY planned_start ASC")
    fun getVisitsByDate(userId: String, date: String): Flow<List<VisitEntity>>

    @Query("SELECT * FROM visits WHERE user_id = :userId AND status = :status ORDER BY planned_date DESC")
    fun getVisitsByStatus(userId: String, status: String): Flow<List<VisitEntity>>

    @Query("SELECT * FROM visits WHERE user_id = :userId ORDER BY planned_date DESC, planned_start ASC")
    fun getAllVisits(userId: String): Flow<List<VisitEntity>>

    @Query("SELECT * FROM visits WHERE user_id = :userId AND planned_date BETWEEN :startDate AND :endDate ORDER BY planned_date ASC")
    fun getVisitsBetweenDates(userId: String, startDate: String, endDate: String): Flow<List<VisitEntity>>

    @Query("SELECT * FROM visits WHERE is_synced = 0 AND sync_status = 'pending'")
    suspend fun getPendingSync(): List<VisitEntity>

    @Query("UPDATE visits SET is_synced = 1, sync_status = 'synced', updated_at = :updatedAt WHERE id = :id")
    suspend fun markSynced(id: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE visits SET sync_status = 'failed', updated_at = :updatedAt WHERE id = :id")
    suspend fun markFailed(id: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM visits WHERE user_id = :userId AND planned_date = :date AND status = :status")
    fun getCountByDateAndStatus(userId: String, date: String, status: String): Flow<Int>

    @Query("DELETE FROM visits WHERE is_synced = 1 AND created_at < :beforeTimestamp")
    suspend fun deleteOldSynced(beforeTimestamp: Long)
}
