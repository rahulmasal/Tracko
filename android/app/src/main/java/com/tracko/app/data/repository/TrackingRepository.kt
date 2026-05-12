package com.tracko.app.data.repository

import android.content.Context
import android.location.Location
import com.tracko.app.data.local.entity.TrackingLogEntity
import com.tracko.app.data.remote.api.TrackingApi
import com.tracko.app.data.remote.dto.TrackingBatchRequest
import com.tracko.app.data.remote.dto.TrackingPointDto
import com.tracko.app.data.remote.interceptor.TokenManager
import com.tracko.app.util.SyncManager
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackingApi: TrackingApi,
    private val tokenManager: TokenManager,
    private val syncManager: SyncManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val gson = Gson()
    private val pendingLocations = mutableListOf<TrackingLogEntity>()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _lastLocation = MutableStateFlow<Location?>(null)
    val lastLocation: StateFlow<Location?> = _lastLocation.asStateFlow()

    fun startTracking() {
        _isTracking.value = true
        scope.launch {
            while (_isTracking.value) {
                if (pendingLocations.size >= 10) {
                    uploadBatch()
                }
                delay(30000)
            }
        }
    }

    fun stopTracking() {
        _isTracking.value = false
        scope.launch {
            if (pendingLocations.isNotEmpty()) {
                uploadBatch()
            }
        }
    }

    suspend fun recordLocation(location: Location, batteryLevel: Float, isMock: Boolean) {
        val entity = TrackingLogEntity(
            userId = tokenManager.getUserId() ?: "unknown",
            latitude = location.latitude,
            longitude = location.longitude,
            accuracy = location.accuracy,
            speed = location.speed,
            bearing = location.bearing,
            altitude = location.altitude,
            batteryLevel = batteryLevel,
            isMock = isMock,
            provider = location.provider,
            recordedAt = System.currentTimeMillis()
        )
        pendingLocations.add(entity)
        _lastLocation.value = location
    }

    suspend fun uploadBatch(): Boolean {
        if (pendingLocations.isEmpty()) return true
        val batch = pendingLocations.toList()
        pendingLocations.clear()

        return try {
            val points = batch.map { it.toPointDto() }
            val request = TrackingBatchRequest(
                points = points,
                userId = tokenManager.getUserId(),
                batchId = UUID.randomUUID().toString()
            )
            val response = trackingApi.uploadLocationBatch(request)
            response.isSuccessful
        } catch (e: Exception) {
            pendingLocations.addAll(batch)
            false
        }
    }

    private fun TrackingLogEntity.toPointDto() = TrackingPointDto(
        latitude = latitude,
        longitude = longitude,
        accuracy = accuracy,
        speed = speed,
        bearing = bearing,
        altitude = altitude,
        batteryLevel = batteryLevel,
        isMock = isMock,
        provider = provider,
        recordedAt = recordedAt
    )
}
