package com.tracko.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AttendanceRequest(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("accuracy") val accuracy: Float? = null,
    @SerializedName("deviceRiskScore") val deviceRiskScore: Int? = null,
    @SerializedName("geoFenceValidated") val geoFenceValidated: Boolean = false,
    @SerializedName("selfie") val selfie: String? = null,
    @SerializedName("remarks") val remarks: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null
)

data class AttendanceResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("userId") val userId: String,
    @SerializedName("employeeName") val employeeName: String? = null,
    @SerializedName("date") val date: String,
    @SerializedName("checkInTime") val checkInTime: String? = null,
    @SerializedName("checkOutTime") val checkOutTime: String? = null,
    @SerializedName("checkInLat") val checkInLat: Double? = null,
    @SerializedName("checkInLng") val checkInLng: Double? = null,
    @SerializedName("status") val status: String,
    @SerializedName("workDuration") val workDuration: String? = null,
    @SerializedName("overtime") val overtime: String? = null,
    @SerializedName("isLate") val isLate: Boolean = false,
    @SerializedName("lateMinutes") val lateMinutes: Int? = null,
    @SerializedName("isEarlyDeparture") val isEarlyDeparture: Boolean = false,
    @SerializedName("remarks") val remarks: String? = null
)
