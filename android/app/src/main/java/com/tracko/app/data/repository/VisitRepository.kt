package com.tracko.app.data.repository

import com.tracko.app.data.local.dao.VisitDao
import com.tracko.app.data.local.entity.VisitEntity
import com.tracko.app.data.remote.api.VisitApi
import com.tracko.app.data.remote.dto.VisitDto
import com.tracko.app.data.remote.interceptor.TokenManager
import com.tracko.app.util.SyncManager
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitRepository @Inject constructor(
    private val visitDao: VisitDao,
    private val visitApi: VisitApi,
    private val tokenManager: TokenManager,
    private val syncManager: SyncManager
) {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun getTodayVisits(userId: String): Flow<List<VisitEntity>> {
        val today = dateFormatter.format(Date())
        return visitDao.getVisitsByDate(userId, today)
    }

    fun getVisitsByDate(userId: String, date: String): Flow<List<VisitEntity>> {
        return visitDao.getVisitsByDate(userId, date)
    }

    fun getVisitByIdFlow(id: Long): Flow<VisitEntity?> {
        return visitDao.getByIdFlow(id)
    }

    suspend fun getVisitById(id: Long): VisitEntity? {
        return visitDao.getById(id)
    }

    fun getAllVisits(userId: String): Flow<List<VisitEntity>> {
        return visitDao.getAllVisits(userId)
    }

    suspend fun checkInVisit(visitId: Long, lat: Double, lng: Double): Result<VisitEntity> {
        return try {
            val visit = visitDao.getById(visitId) ?: return Result.failure(Exception("Visit not found"))
            val now = System.currentTimeMillis()
            val updated = visit.copy(
                actualCheckIn = now.toString(),
                status = "in_progress",
                updatedAt = now,
                isSynced = false,
                syncStatus = "pending"
            )
            visitDao.update(updated)
            syncManager.addToQueue("visit", updated.id, "UPDATE")

            try {
                val response = visitApi.checkInVisit(visitId, mapOf("lat" to lat, "lng" to lng))
                if (response.isSuccessful) visitDao.markSynced(updated.id)
            } catch (_: Exception) { }

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkOutVisit(visitId: Long, lat: Double, lng: Double, remarks: String? = null): Result<VisitEntity> {
        return try {
            val visit = visitDao.getById(visitId) ?: return Result.failure(Exception("Visit not found"))
            val checkInTime = visit.actualCheckIn?.toLongOrNull() ?: System.currentTimeMillis()
            val now = System.currentTimeMillis()
            val timeOnSite = ((now - checkInTime) / 60000).toInt()

            val updated = visit.copy(
                actualCheckOut = now.toString(),
                status = if (remarks != null) "completed_with_remarks" else "completed",
                timeOnSiteMinutes = timeOnSite,
                remarks = remarks,
                updatedAt = now,
                isSynced = false,
                syncStatus = "pending"
            )
            visitDao.update(updated)
            syncManager.addToQueue("visit", updated.id, "UPDATE")

            try {
                val response = visitApi.checkOutVisit(
                    visitId,
                    mapOf("lat" to lat, "lng" to lng, "remarks" to (remarks ?: ""))
                )
                if (response.isSuccessful) visitDao.markSynced(updated.id)
            } catch (_: Exception) { }

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncVisitsFromServer(userId: String) {
        try {
            val response = visitApi.getTodayVisits()
            if (response.isSuccessful && response.body()?.success == true) {
                val visits = response.body()!!.data ?: return
                val entities = visits.map { it.toEntity(userId) }
                visitDao.insertAll(entities)
            }
        } catch (_: Exception) { }
    }

    suspend fun syncPendingVisits() {
        val pending = visitDao.getPendingSync()
        for (visit in pending) {
            try {
                val response = when (visit.status) {
                    "in_progress" -> visitApi.checkInVisit(visit.serverId?.toLongOrNull() ?: visit.id, mapOf("lat" to 0.0, "lng" to 0.0))
                    "completed" -> visitApi.checkOutVisit(visit.serverId?.toLongOrNull() ?: visit.id, mapOf("lat" to 0.0, "lng" to 0.0))
                    else -> null
                }
                if (response?.isSuccessful == true) {
                    visitDao.markSynced(visit.id)
                } else {
                    visitDao.markFailed(visit.id)
                }
            } catch (e: Exception) {
                visitDao.markFailed(visit.id)
            }
        }
    }

    private fun VisitDto.toEntity(userId: String) = VisitEntity(
        visitNumber = visitNumber ?: "",
        userId = userId,
        customerId = customerId,
        customerName = customerName,
        siteAddress = siteAddress,
        siteLat = siteLat,
        siteLng = siteLng,
        plannedDate = plannedDate,
        plannedStart = plannedStart,
        plannedEnd = plannedEnd,
        actualCheckIn = actualCheckIn,
        actualCheckOut = actualCheckOut,
        visitType = visitType ?: "regular",
        priority = priority ?: "normal",
        status = status ?: "planned",
        ticketNumber = ticketNumber,
        timeOnSiteMinutes = timeOnSiteMinutes,
        customerPhone = customerPhone,
        remarks = remarks,
        isSynced = id != null,
        syncStatus = if (id != null) "synced" else "pending",
        serverId = id?.toString()
    )
}
