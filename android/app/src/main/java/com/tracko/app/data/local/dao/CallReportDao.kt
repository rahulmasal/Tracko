package com.tracko.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tracko.app.data.local.entity.CallReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CallReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: CallReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reports: List<CallReportEntity>)

    @Update
    suspend fun update(report: CallReportEntity)

    @Query("SELECT * FROM call_reports WHERE id = :id")
    suspend fun getById(id: Long): CallReportEntity?

    @Query("SELECT * FROM call_reports WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<CallReportEntity?>

    @Query("SELECT * FROM call_reports WHERE server_id = :serverId LIMIT 1")
    suspend fun getByServerId(serverId: String): CallReportEntity?

    @Query("SELECT * FROM call_reports WHERE visit_id = :visitId LIMIT 1")
    suspend fun getByVisitId(visitId: Long): CallReportEntity?

    @Query("SELECT * FROM call_reports WHERE user_id = :userId ORDER BY visit_date DESC, created_at DESC")
    fun getReportsByUser(userId: String): Flow<List<CallReportEntity>>

    @Query("SELECT * FROM call_reports WHERE user_id = :userId AND submission_status = :status ORDER BY visit_date DESC")
    fun getReportsByStatus(userId: String, status: String): Flow<List<CallReportEntity>>

    @Query("SELECT * FROM call_reports WHERE user_id = :userId AND visit_date BETWEEN :startDate AND :endDate ORDER BY visit_date DESC")
    fun getReportsBetweenDates(userId: String, startDate: String, endDate: String): Flow<List<CallReportEntity>>

    @Query("SELECT * FROM call_reports WHERE customer_name LIKE '%' || :query || '%' OR report_number LIKE '%' || :query || '%' ORDER BY visit_date DESC")
    fun searchReports(query: String): Flow<List<CallReportEntity>>

    @Query("SELECT * FROM call_reports WHERE is_synced = 0 AND sync_status = 'pending'")
    suspend fun getPendingSync(): List<CallReportEntity>

    @Query("UPDATE call_reports SET is_synced = 1, sync_status = 'synced', updated_at = :updatedAt WHERE id = :id")
    suspend fun markSynced(id: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE call_reports SET sync_status = 'failed', updated_at = :updatedAt WHERE id = :id")
    suspend fun markFailed(id: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM call_reports WHERE user_id = :userId AND submission_status = 'submitted'")
    fun getSubmittedCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM call_reports WHERE user_id = :userId AND submission_status = 'draft'")
    fun getDraftCount(userId: String): Flow<Int>

    @Query("DELETE FROM call_reports WHERE is_synced = 1 AND created_at < :beforeTimestamp")
    suspend fun deleteOldSynced(beforeTimestamp: Long)
}
