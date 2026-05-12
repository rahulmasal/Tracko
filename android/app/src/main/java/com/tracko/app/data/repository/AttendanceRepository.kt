package com.tracko.app.data.repository

import android.location.Location
import com.tracko.app.data.local.dao.AttendanceDao
import com.tracko.app.data.local.entity.AttendanceEntity
import com.tracko.app.data.remote.api.AttendanceApi
import com.tracko.app.data.remote.dto.AttendanceRequest
import com.tracko.app.data.remote.interceptor.TokenManager
import com.tracko.app.util.SyncManager
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val attendanceApi: AttendanceApi,
    private val tokenManager: TokenManager,
    private val syncManager: SyncManager
) {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    suspend fun checkIn(
        location: Location,
        deviceRiskScore: Int,
        geoFenceValidated: Boolean,
        selfieBase64: String? = null,
        remarks: String? = null
    ): Result<AttendanceEntity> {
        return try {
            val userId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
            val today = dateFormatter.format(Date())
            val now = System.currentTimeMillis()

            val entity = AttendanceEntity(
                userId = userId,
                date = today,
                checkInTime = now.toString(),
                checkInLat = location.latitude,
                checkInLng = location.longitude,
                checkInAccuracy = location.accuracy,
                status = "present",
                deviceRiskScore = deviceRiskScore,
                geoFenceValidated = geoFenceValidated,
                selfieUrl = selfieBase64,
                isSynced = false,
                syncStatus = "pending"
            )

            val id = attendanceDao.insert(entity)
            val saved = attendanceDao.getById(id) ?: return Result.failure(Exception("Failed to save attendance"))

            syncManager.addToQueue("attendance", id, "CREATE")

            try {
                val response = attendanceApi.checkIn(
                    AttendanceRequest(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        deviceRiskScore = deviceRiskScore,
                        geoFenceValidated = geoFenceValidated,
                        selfie = selfieBase64,
                        remarks = remarks
                    )
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    attendanceDao.markSynced(id)
                }
            } catch (_: Exception) { }

            Result.success(saved)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkOut(
        location: Location,
        remarks: String? = null,
        selfieBase64: String? = null
    ): Result<AttendanceEntity> {
        return try {
            val userId = tokenManager.getUserId() ?: return Result.failure(Exception("User not logged in"))
            val today = dateFormatter.format(Date())

            val existing = attendanceDao.getByUserAndDate(userId, today)
            if (existing == null) {
                return Result.failure(Exception("No check-in found for today"))
            }

            val updated = existing.copy(
                checkOutTime = System.currentTimeMillis().toString(),
                checkOutLat = location.latitude,
                checkOutLng = location.longitude,
                endOfDayRemarks = remarks,
                checkOutSelfieUrl = selfieBase64,
                updatedAt = System.currentTimeMillis(),
                isSynced = false,
                syncStatus = "pending"
            )
            attendanceDao.update(updated)

            syncManager.addToQueue("attendance", updated.id, "UPDATE")

            try {
                val response = attendanceApi.checkOut(
                    AttendanceRequest(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        remarks = remarks
                    )
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    attendanceDao.markSynced(updated.id)
                }
            } catch (_: Exception) { }

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getTodayAttendance(userId: String): Flow<AttendanceEntity?> {
        val today = dateFormatter.format(Date())
        return attendanceDao.getTodayAttendance(userId, today)
    }

    suspend fun getAttendanceForDate(userId: String, date: String): AttendanceEntity? {
        return attendanceDao.getByUserAndDate(userId, date)
    }

    fun getAttendanceHistory(userId: String): Flow<List<AttendanceEntity>> {
        return attendanceDao.getByUser(userId)
    }

    fun getPresentCountForMonth(userId: String, monthPattern: String): Flow<Int> {
        return attendanceDao.getPresentCountForMonth(userId, monthPattern)
    }

    suspend fun syncPendingAttendance() {
        val pending = attendanceDao.getPendingSync()
        for (item in pending) {
            try {
                val location = android.location.Location("").apply {
                    latitude = item.checkInLat ?: 0.0
                    longitude = item.checkInLng ?: 0.0
                    accuracy = item.checkInAccuracy ?: 0f
                }
                val request = AttendanceRequest(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracy = location.accuracy
                )
                val response = if (item.checkInTime != null && item.checkOutTime == null) {
                    attendanceApi.checkIn(request)
                } else {
                    attendanceApi.checkOut(request)
                }
                if (response.isSuccessful) {
                    attendanceDao.markSynced(item.id)
                } else {
                    attendanceDao.markFailed(item.id, response.message())
                }
            } catch (e: Exception) {
                attendanceDao.markFailed(item.id, e.localizedMessage ?: "Sync error")
            }
        }
    }
}
