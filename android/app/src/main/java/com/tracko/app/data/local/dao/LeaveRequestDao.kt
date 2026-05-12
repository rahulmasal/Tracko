package com.tracko.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tracko.app.data.local.entity.LeaveRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaveRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(leave: LeaveRequestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(leaves: List<LeaveRequestEntity>)

    @Update
    suspend fun update(leave: LeaveRequestEntity)

    @Query("SELECT * FROM leave_requests WHERE id = :id")
    suspend fun getById(id: Long): LeaveRequestEntity?

    @Query("SELECT * FROM leave_requests WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<LeaveRequestEntity?>

    @Query("SELECT * FROM leave_requests WHERE server_id = :serverId LIMIT 1")
    suspend fun getByServerId(serverId: String): LeaveRequestEntity?

    @Query("SELECT * FROM leave_requests WHERE user_id = :userId ORDER BY created_at DESC")
    fun getLeavesByUser(userId: String): Flow<List<LeaveRequestEntity>>

    @Query("SELECT * FROM leave_requests WHERE user_id = :userId AND status = :status ORDER BY created_at DESC")
    fun getLeavesByStatus(userId: String, status: String): Flow<List<LeaveRequestEntity>>

    @Query("SELECT * FROM leave_requests WHERE user_id = :userId AND start_date BETWEEN :startDate AND :endDate ORDER BY start_date ASC")
    fun getLeavesBetweenDates(userId: String, startDate: String, endDate: String): Flow<List<LeaveRequestEntity>>

    @Query("SELECT * FROM leave_requests WHERE user_id = :userId AND leave_type = :leaveType AND status = 'approved' AND end_date >= :currentDate")
    suspend fun getActiveLeavesByType(userId: String, leaveType: String, currentDate: String): List<LeaveRequestEntity>

    @Query("SELECT SUM(total_days) FROM leave_requests WHERE user_id = :userId AND leave_type = :leaveType AND status IN ('approved', 'pending') AND strftime('%m', start_date) = strftime('%m', 'now') AND strftime('%Y', start_date) = strftime('%Y', 'now')")
    fun getLeaveUsedThisMonth(userId: String, leaveType: String): Flow<Double?>

    @Query("SELECT * FROM leave_requests WHERE is_synced = 0 AND sync_status = 'pending'")
    suspend fun getPendingSync(): List<LeaveRequestEntity>

    @Query("UPDATE leave_requests SET is_synced = 1, sync_status = 'synced', updated_at = :updatedAt WHERE id = :id")
    suspend fun markSynced(id: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE leave_requests SET sync_status = 'failed', updated_at = :updatedAt WHERE id = :id")
    suspend fun markFailed(id: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM leave_requests WHERE is_synced = 1 AND created_at < :beforeTimestamp")
    suspend fun deleteOldSynced(beforeTimestamp: Long)
}
