package com.tracko.app.data.repository

import com.tracko.app.data.local.dao.NotificationDao
import com.tracko.app.data.local.entity.NotificationEntity
import com.tracko.app.data.remote.api.NotificationApi
import com.tracko.app.data.remote.dto.NotificationDto
import com.tracko.app.data.remote.interceptor.TokenManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao,
    private val notificationApi: NotificationApi,
    private val tokenManager: TokenManager
) {

    fun getNotifications(userId: String): Flow<List<NotificationEntity>> {
        return notificationDao.getNotificationsByUser(userId)
    }

    fun getUnreadCount(userId: String): Flow<Int> {
        return notificationDao.getUnreadCount(userId)
    }

    suspend fun getUnreadCountOnce(userId: String): Int {
        return notificationDao.getUnreadCountOnce(userId)
    }

    suspend fun markAsRead(id: Long) {
        notificationDao.markAsRead(id)
        try {
            val notification = notificationDao.getById(id)
            notification?.notificationId?.let { notificationApi.markRead(it) }
        } catch (_: Exception) { }
    }

    suspend fun markAllAsRead(userId: String) {
        notificationDao.markAllAsRead(userId)
        try {
            notificationApi.markAllRead()
        } catch (_: Exception) { }
    }

    suspend fun fetchNotificationsFromServer(userId: String) {
        try {
            val response = notificationApi.getNotifications()
            if (response.isSuccessful && response.body()?.success == true) {
                val dtos = response.body()!!.data?.content ?: return
                val entities = dtos.map { it.toEntity(userId) }
                for (entity in entities) {
                    val existing = notificationDao.getByNotificationId(entity.notificationId)
                    if (existing == null) {
                        notificationDao.insert(entity)
                    }
                }
            }
        } catch (_: Exception) { }
    }

    private fun NotificationDto.toEntity(userId: String) = NotificationEntity(
        notificationId = id,
        userId = userId,
        title = title,
        body = body,
        type = type,
        referenceId = referenceId,
        referenceType = referenceType,
        isRead = isRead,
        priority = priority ?: "normal",
        imageUrl = imageUrl,
        actionUrl = actionUrl,
        receivedAt = try {
            java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).parse(receivedAt)?.time ?: System.currentTimeMillis()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    )
}
