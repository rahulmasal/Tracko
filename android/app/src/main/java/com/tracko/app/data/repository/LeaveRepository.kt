package com.tracko.app.data.repository

import com.tracko.app.data.local.dao.LeaveRequestDao
import com.tracko.app.data.local.entity.LeaveRequestEntity
import com.tracko.app.data.remote.api.LeaveApi
import com.tracko.app.data.remote.dto.LeaveBalanceResponse
import com.tracko.app.data.remote.dto.LeaveRequestDto
import com.tracko.app.data.remote.interceptor.TokenManager
import com.tracko.app.util.SyncManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

sealed class LeaveBalanceResult {
    data class Success(val balance: LeaveBalanceResponse) : LeaveBalanceResult()
    data class Error(val message: String) : LeaveBalanceResult()
}

@Singleton
class LeaveRepository @Inject constructor(
    private val leaveRequestDao: LeaveRequestDao,
    private val leaveApi: LeaveApi,
    private val tokenManager: TokenManager,
    private val syncManager: SyncManager
) {

    suspend fun applyLeave(entity: LeaveRequestEntity): Result<LeaveRequestEntity> {
        return try {
            val userId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
            val leaveNumber = "LV-${System.currentTimeMillis()}"
            val saved = entity.copy(
                userId = userId,
                leaveNumber = leaveNumber,
                status = "pending",
                isSynced = false,
                syncStatus = "pending"
            )
            val id = leaveRequestDao.insert(saved)
            val result = leaveRequestDao.getById(id) ?: return Result.failure(Exception("Failed to save leave"))
            syncManager.addToQueue("leave_request", id, "CREATE")

            try {
                val dto = result.toDto()
                leaveApi.applyLeave(dto)
            } catch (_: Exception) { }

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelLeave(id: Long): Result<LeaveRequestEntity> {
        return try {
            val entity = leaveRequestDao.getById(id) ?: return Result.failure(Exception("Leave not found"))
            val updated = entity.copy(
                status = "cancelled",
                updatedAt = System.currentTimeMillis(),
                isSynced = false,
                syncStatus = "pending"
            )
            leaveRequestDao.update(updated)

            try {
                leaveApi.cancelLeave(entity.serverId?.toLongOrNull() ?: id)
            } catch (_: Exception) { }

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getLeavesByUser(userId: String): Flow<List<LeaveRequestEntity>> {
        return leaveRequestDao.getLeavesByUser(userId)
    }

    fun getLeavesByStatus(userId: String, status: String): Flow<List<LeaveRequestEntity>> {
        return leaveRequestDao.getLeavesByStatus(userId, status)
    }

    fun getLeaveByIdFlow(id: Long): Flow<LeaveRequestEntity?> {
        return leaveRequestDao.getByIdFlow(id)
    }

    suspend fun getLeaveBalance(): LeaveBalanceResult {
        return try {
            val response = leaveApi.getLeaveBalance()
            if (response.isSuccessful && response.body()?.success == true) {
                val balance = response.body()!!.data!!
                LeaveBalanceResult.Success(balance)
            } else {
                LeaveBalanceResult.Error(response.body()?.message ?: "Failed to fetch balance")
            }
        } catch (e: Exception) {
            LeaveBalanceResult.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun syncPendingLeaves() {
        val pending = leaveRequestDao.getPendingSync()
        for (item in pending) {
            try {
                val dto = item.toDto()
                val response = leaveApi.applyLeave(dto)
                if (response.isSuccessful) {
                    leaveRequestDao.markSynced(item.id)
                } else {
                    leaveRequestDao.markFailed(item.id)
                }
            } catch (e: Exception) {
                leaveRequestDao.markFailed(item.id)
            }
        }
    }

    private fun LeaveRequestEntity.toDto() = LeaveRequestDto(
        leaveType = leaveType,
        startDate = startDate,
        endDate = endDate,
        totalDays = totalDays,
        reason = reason,
        contactDuringLeave = contactDuringLeave,
        status = status
    )
}
