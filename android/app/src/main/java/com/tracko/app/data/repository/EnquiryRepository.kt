package com.tracko.app.data.repository

import com.tracko.app.data.local.dao.EnquiryDao
import com.tracko.app.data.local.entity.EnquiryEntity
import com.tracko.app.data.remote.api.EnquiryApi
import com.tracko.app.data.remote.dto.EnquiryDto
import com.tracko.app.data.remote.dto.FollowupDto
import com.tracko.app.data.remote.dto.UpdateEnquiryStatusRequest
import com.tracko.app.data.remote.interceptor.TokenManager
import com.tracko.app.util.SyncManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnquiryRepository @Inject constructor(
    private val enquiryDao: EnquiryDao,
    private val enquiryApi: EnquiryApi,
    private val tokenManager: TokenManager,
    private val syncManager: SyncManager
) {
    private val gson = Gson()

    suspend fun createEnquiry(entity: EnquiryEntity): Result<EnquiryEntity> {
        return try {
            val userId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
            val enquiryNumber = "ENQ-${System.currentTimeMillis()}"
            val saved = entity.copy(
                userId = userId,
                enquiryNumber = enquiryNumber,
                status = "new",
                isSynced = false,
                syncStatus = "pending"
            )
            val id = enquiryDao.insert(saved)
            val result = enquiryDao.getById(id) ?: return Result.failure(Exception("Failed to save enquiry"))
            syncManager.addToQueue("enquiry", id, "CREATE")

            try {
                val dto = result.toDto()
                enquiryApi.createEnquiry(dto)
            } catch (_: Exception) { }

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEnquiry(entity: EnquiryEntity): Result<EnquiryEntity> {
        return try {
            val updated = entity.copy(
                updatedAt = System.currentTimeMillis(),
                isSynced = false,
                syncStatus = "pending"
            )
            enquiryDao.update(updated)
            syncManager.addToQueue("enquiry", updated.id, "UPDATE")

            try {
                val dto = updated.toDto()
                enquiryApi.updateEnquiry(updated.serverId?.toLongOrNull() ?: updated.id, dto)
            } catch (_: Exception) { }

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStatus(id: Long, newStatus: String, notes: String? = null): Result<EnquiryEntity> {
        return try {
            val entity = enquiryDao.getById(id) ?: return Result.failure(Exception("Enquiry not found"))
            val updated = entity.copy(
                status = newStatus,
                closureNotes = notes,
                updatedAt = System.currentTimeMillis(),
                isSynced = false,
                syncStatus = "pending"
            )
            enquiryDao.update(updated)

            try {
                enquiryApi.updateStatus(
                    updated.serverId?.toLongOrNull() ?: id,
                    UpdateEnquiryStatusRequest(newStatus, notes)
                )
            } catch (_: Exception) { }

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFollowup(id: Long, notes: String): Result<EnquiryEntity> {
        return try {
            val entity = enquiryDao.getById(id) ?: return Result.failure(Exception("Enquiry not found"))
            val existingFollowups = entity.followupData?.let {
                try {
                    gson.fromJson(it, object : TypeToken<List<FollowupDto>>() {}.type) as? List<FollowupDto>
                } catch (_: Exception) { null }
            } ?: emptyList()

            val newFollowup = FollowupDto(
                notes = notes,
                followupByName = tokenManager.getUserId(),
                followupAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).format(java.util.Date())
            )

            val allFollowups = existingFollowups + newFollowup
            val updated = entity.copy(
                followupData = gson.toJson(allFollowups),
                updatedAt = System.currentTimeMillis(),
                isSynced = false,
                syncStatus = "pending"
            )
            enquiryDao.update(updated)
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getEnquiriesByUser(userId: String): Flow<List<EnquiryEntity>> {
        return enquiryDao.getEnquiriesByUser(userId)
    }

    fun getEnquiriesByStatus(userId: String, status: String): Flow<List<EnquiryEntity>> {
        return enquiryDao.getEnquiriesByStatus(userId, status)
    }

    fun getEnquiryByIdFlow(id: Long): Flow<EnquiryEntity?> {
        return enquiryDao.getByIdFlow(id)
    }

    suspend fun syncPendingEnquiries() {
        val pending = enquiryDao.getPendingSync()
        for (item in pending) {
            try {
                val response = enquiryApi.createEnquiry(item.toDto())
                if (response.isSuccessful) {
                    enquiryDao.markSynced(item.id)
                } else {
                    enquiryDao.markFailed(item.id)
                }
            } catch (e: Exception) {
                enquiryDao.markFailed(item.id)
            }
        }
    }

    private fun EnquiryEntity.toDto() = EnquiryDto(
        customerId = customerId,
        customerName = customerName,
        customerPhone = customerPhone,
        customerEmail = customerEmail,
        contactPerson = contactPerson,
        contactNumber = contactNumber,
        requirementDescription = requirementDescription,
        problemStatement = problemStatement,
        category = category,
        priority = priority,
        budgetRange = budgetRange,
        source = source,
        status = status,
        notes = notes
    )
}
