package com.tracko.app.data.repository

import com.tracko.app.data.local.dao.CallReportDao
import com.tracko.app.data.local.entity.CallReportEntity
import com.tracko.app.data.remote.api.CallReportApi
import com.tracko.app.data.remote.dto.CallReportDto
import com.tracko.app.data.remote.interceptor.TokenManager
import com.tracko.app.util.SyncManager
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallReportRepository @Inject constructor(
    private val callReportDao: CallReportDao,
    private val callReportApi: CallReportApi,
    private val tokenManager: TokenManager,
    private val syncManager: SyncManager
) {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    suspend fun saveDraft(report: CallReportEntity): Result<CallReportEntity> {
        return try {
            val userId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
            val entity = report.copy(
                userId = userId,
                submissionStatus = "draft",
                isSynced = false,
                syncStatus = "pending"
            )
            val id = if (entity.id == 0L) {
                callReportDao.insert(entity)
            } else {
                callReportDao.update(entity)
                entity.id
            }
            val saved = callReportDao.getById(id) ?: return Result.failure(Exception("Failed to save report"))
            syncManager.addToQueue("call_report", id, if (report.id == 0L) "CREATE" else "UPDATE")
            Result.success(saved)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitReport(report: CallReportEntity): Result<CallReportEntity> {
        return try {
            val userId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
            val reportNumber = "RPT-${System.currentTimeMillis()}"
            val entity = report.copy(
                userId = userId,
                reportNumber = reportNumber,
                submissionStatus = "submitted",
                isSynced = false,
                syncStatus = "pending"
            )
            val id = if (entity.id == 0L) {
                callReportDao.insert(entity)
            } else {
                callReportDao.update(entity)
                entity.id
            }
            val saved = callReportDao.getById(id) ?: return Result.failure(Exception("Failed to submit report"))
            syncManager.addToQueue("call_report", id, "CREATE")

            try {
                val dto = saved.toDto()
                val response = callReportApi.createReport(dto)
                if (response.isSuccessful && response.body()?.success == true) {
                    callReportDao.markSynced(id)
                }
            } catch (_: Exception) { }

            Result.success(saved)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getReportsByUser(userId: String): Flow<List<CallReportEntity>> {
        return callReportDao.getReportsByUser(userId)
    }

    fun getReportByIdFlow(id: Long): Flow<CallReportEntity?> {
        return callReportDao.getByIdFlow(id)
    }

    suspend fun getReportById(id: Long): CallReportEntity? {
        return callReportDao.getById(id)
    }

    fun searchReports(query: String): Flow<List<CallReportEntity>> {
        return callReportDao.searchReports(query)
    }

    suspend fun syncPendingReports() {
        val pending = callReportDao.getPendingSync()
        for (report in pending) {
            try {
                val dto = report.toDto()
                val response = callReportApi.createReport(dto)
                if (response.isSuccessful) {
                    callReportDao.markSynced(report.id)
                } else {
                    callReportDao.markFailed(report.id)
                }
            } catch (e: Exception) {
                callReportDao.markFailed(report.id)
            }
        }
    }

    private fun CallReportEntity.toDto() = CallReportDto(
        reportNumber = reportNumber,
        visitId = visitId,
        customerId = customerId,
        customerName = customerName,
        contactPerson = contactPerson,
        contactNumber = contactNumber,
        siteName = siteName,
        siteAddress = siteAddress,
        visitDate = visitDate,
        visitType = visitType,
        problemReported = problemReported,
        observation = observation,
        workDone = workDone,
        partsUsed = partsUsed,
        resolutionStatus = resolutionStatus,
        pendingIssue = pendingIssue,
        nextAction = nextAction,
        nextFollowupDate = nextFollowupDate,
        timeSpentMinutes = timeSpentMinutes,
        customerRemarks = customerRemarks,
        customerRating = customerRating,
        engineerRemarks = engineerRemarks,
        submissionStatus = submissionStatus,
        signatureUrl = signatureUrl,
        pdfUrl = pdfUrl
    )
}
