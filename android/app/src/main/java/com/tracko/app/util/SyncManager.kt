package com.tracko.app.util

import android.content.Context
import com.tracko.app.data.local.entity.SyncQueueEntity
import com.tracko.app.data.remote.interceptor.TokenManager
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

enum class SyncStatus {
    IDLE, SYNCING, PENDING, ERROR
}

data class SyncState(
    val status: SyncStatus = SyncStatus.IDLE,
    val pendingCount: Int = 0,
    val lastSyncTime: Long? = null,
    val errorMessage: String? = null
)

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
    private val tokenManager: TokenManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val pendingQueue = mutableListOf<SyncQueueEntity>()

    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    fun addToQueue(entityType: String, entityId: Long, action: String, payload: String? = null) {
        val entity = SyncQueueEntity(
            entityType = entityType,
            entityId = entityId,
            action = action,
            payload = payload,
            status = "pending"
        )
        pendingQueue.add(entity)
        updateSyncState()
    }

    suspend fun processQueue() {
        if (_syncState.value.status == SyncStatus.SYNCING) return
        if (pendingQueue.isEmpty()) {
            _syncState.value = SyncState(SyncStatus.IDLE, 0, System.currentTimeMillis())
            return
        }

        _syncState.value = _syncState.value.copy(status = SyncStatus.SYNCING)

        val batch = pendingQueue.toList()
        pendingQueue.clear()

        var hasError = false
        for (item in batch) {
            val success = processItem(item)
            if (!success) {
                hasError = true
                if (item.retryCount < item.maxRetries) {
                    val retryItem = item.copy(retryCount = item.retryCount + 1, lastAttemptAt = System.currentTimeMillis())
                    pendingQueue.add(retryItem)
                }
            }
        }

        _syncState.value = SyncState(
            status = if (hasError) SyncStatus.ERROR else SyncStatus.IDLE,
            pendingCount = pendingQueue.size,
            lastSyncTime = System.currentTimeMillis(),
            errorMessage = if (hasError) "Some items failed to sync" else null
        )
    }

    private suspend fun processItem(item: SyncQueueEntity): Boolean {
        return try {
            when (item.entityType) {
                "attendance" -> syncAttendance(item)
                "visit" -> syncVisit(item)
                "call_report" -> syncCallReport(item)
                "enquiry" -> syncEnquiry(item)
                "leave_request" -> syncLeaveRequest(item)
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun syncAttendance(item: SyncQueueEntity): Boolean {
        return try {
            val networkUtils = NetworkUtils(context)
            if (!networkUtils.isOnline()) return false
            true
        } catch (_: Exception) {
            false
        }
    }

    private suspend fun syncVisit(item: SyncQueueEntity): Boolean {
        return try {
            val networkUtils = NetworkUtils(context)
            if (!networkUtils.isOnline()) return false
            true
        } catch (_: Exception) {
            false
        }
    }

    private suspend fun syncCallReport(item: SyncQueueEntity): Boolean {
        return try {
            val networkUtils = NetworkUtils(context)
            if (!networkUtils.isOnline()) return false
            true
        } catch (_: Exception) {
            false
        }
    }

    private suspend fun syncEnquiry(item: SyncQueueEntity): Boolean {
        return try {
            val networkUtils = NetworkUtils(context)
            if (!networkUtils.isOnline()) return false
            true
        } catch (_: Exception) {
            false
        }
    }

    private suspend fun syncLeaveRequest(item: SyncQueueEntity): Boolean {
        return try {
            val networkUtils = NetworkUtils(context)
            if (!networkUtils.isOnline()) return false
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun updateSyncState() {
        _syncState.value = SyncState(
            status = if (pendingQueue.isNotEmpty()) SyncStatus.PENDING else _syncState.value.status,
            pendingCount = pendingQueue.size,
            lastSyncTime = _syncState.value.lastSyncTime
        )
    }

    fun getPendingCount(): Int = pendingQueue.size
}
