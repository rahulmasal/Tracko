package com.tracko.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tracko.app.data.local.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: AttendanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(attendanceList: List<AttendanceEntity>)

    @Update
    suspend fun update(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendance WHERE id = :id")
    suspend fun getById(id: Long): AttendanceEntity?

    @Query("SELECT * FROM attendance WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<AttendanceEntity?>

    @Query("SELECT * FROM attendance WHERE user_id = :userId AND date = :date LIMIT 1")
    suspend fun getByUserAndDate(userId: String, date: String): AttendanceEntity?

    @Query("SELECT * FROM attendance WHERE date = :date ORDER BY created_at DESC")
    fun getByDate(date: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE user_id = :userId ORDER BY date DESC, created_at DESC")
    fun getByUser(userId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getAllBetweenDates(startDate: String, endDate: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE is_synced = 0 AND sync_status = 'pending'")
    suspend fun getPendingSync(): List<AttendanceEntity>

    @Query("SELECT * FROM attendance WHERE is_synced = 0 AND sync_status = 'failed'")
    suspend fun getFailedSync(): List<AttendanceEntity>

    @Query("UPDATE attendance SET is_synced = 1, sync_status = 'synced', updated_at = :updatedAt WHERE id = :id")
    suspend fun markSynced(id: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE attendance SET sync_status = 'failed', error_message = :error, updated_at = :updatedAt WHERE id = :id")
    suspend fun markFailed(id: Long, error: String, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM attendance WHERE user_id = :userId AND date = :date LIMIT 1")
    fun getTodayAttendance(userId: String, date: String): Flow<AttendanceEntity?>

    @Query("SELECT COUNT(*) FROM attendance WHERE user_id = :userId AND status = 'present' AND date LIKE :monthPattern")
    fun getPresentCountForMonth(userId: String, monthPattern: String): Flow<Int>

    @Query("DELETE FROM attendance WHERE is_synced = 1 AND created_at < :beforeTimestamp")
    suspend fun deleteOldSynced(beforeTimestamp: Long)
}
