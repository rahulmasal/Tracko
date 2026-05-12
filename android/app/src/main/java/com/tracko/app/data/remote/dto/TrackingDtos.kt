package com.tracko.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TrackingBatchRequest(
    @SerializedName("points") val points: List<TrackingPointDto>,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("batchId") val batchId: String? = null
)

data class TrackingPointDto(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("accuracy") val accuracy: Float? = null,
    @SerializedName("speed") val speed: Float? = null,
    @SerializedName("bearing") val bearing: Float? = null,
    @SerializedName("altitude") val altitude: Double? = null,
    @SerializedName("batteryLevel") val batteryLevel: Float? = null,
    @SerializedName("isMock") val isMock: Boolean = false,
    @SerializedName("provider") val provider: String? = null,
    @SerializedName("recordedAt") val recordedAt: Long
)

data class TrackingStatsResponse(
    @SerializedName("totalDistance") val totalDistance: Double,
    @SerializedName("totalTime") val totalTime: Long,
    @SerializedName("averageSpeed") val averageSpeed: Double,
    @SerializedName("maxSpeed") val maxSpeed: Double,
    @SerializedName("pointCount") val pointCount: Int,
    @SerializedName("startTime") val startTime: String? = null,
    @SerializedName("endTime") val endTime: String? = null
)
