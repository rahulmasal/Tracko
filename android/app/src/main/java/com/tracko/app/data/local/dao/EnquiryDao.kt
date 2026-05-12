package com.tracko.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tracko.app.data.local.entity.EnquiryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EnquiryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(enquiry: EnquiryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(enquiries: List<EnquiryEntity>)

    @Update
    suspend fun update(enquiry: EnquiryEntity)

    @Query("SELECT * FROM enquiries WHERE id = :id")
    suspend fun getById(id: Long): EnquiryEntity?

    @Query("SELECT * FROM enquiries WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<EnquiryEntity?>

    @Query("SELECT * FROM enquiries WHERE server_id = :serverId LIMIT 1")
    suspend fun getByServerId(serverId: String): EnquiryEntity?

    @Query("SELECT * FROM enquiries WHERE user_id = :userId ORDER BY created_at DESC")
    fun getEnquiriesByUser(userId: String): Flow<List<EnquiryEntity>>

    @Query("SELECT * FROM enquiries WHERE user_id = :userId AND status = :status ORDER BY created_at DESC")
    fun getEnquiriesByStatus(userId: String, status: String): Flow<List<EnquiryEntity>>

    @Query("SELECT * FROM enquiries WHERE assigned_to = :userId ORDER BY created_at DESC")
    fun getEnquiriesAssignedTo(userId: String): Flow<List<EnquiryEntity>>

    @Query("SELECT * FROM enquiries WHERE customer_name LIKE '%' || :query || '%' OR enquiry_number LIKE '%' || :query || '%' ORDER BY created_at DESC")
    fun searchEnquiries(query: String): Flow<List<EnquiryEntity>>

    @Query("SELECT * FROM enquiries WHERE is_synced = 0 AND sync_status = 'pending'")
    suspend fun getPendingSync(): List<EnquiryEntity>

    @Query("UPDATE enquiries SET is_synced = 1, sync_status = 'synced', updated_at = :updatedAt WHERE id = :id")
    suspend fun markSynced(id: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE enquiries SET sync_status = 'failed', updated_at = :updatedAt WHERE id = :id")
    suspend fun markFailed(id: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM enquiries WHERE is_synced = 1 AND created_at < :beforeTimestamp")
    suspend fun deleteOldSynced(beforeTimestamp: Long)
}
