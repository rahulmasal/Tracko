package com.tracko.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tracko.app.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)

    @Update
    suspend fun update(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getById(id: Long): NotificationEntity?

    @Query("SELECT * FROM notifications WHERE notification_id = :notificationId LIMIT 1")
    suspend fun getByNotificationId(notificationId: String): NotificationEntity?

    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY received_at DESC")
    fun getNotificationsByUser(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE user_id = :userId AND is_read = 0 ORDER BY received_at DESC")
    fun getUnreadNotifications(userId: String): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET is_read = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET is_read = 1 WHERE user_id = :userId AND is_read = 0")
    suspend fun markAllAsRead(userId: String)

    @Query("SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = 0")
    fun getUnreadCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = 0")
    suspend fun getUnreadCountOnce(userId: String): Int

    @Query("DELETE FROM notifications WHERE received_at < :beforeTimestamp")
    suspend fun deleteOldNotifications(beforeTimestamp: Long)
}
