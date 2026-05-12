package com.tracko.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "check_in_time") val checkInTime: String? = null,
    @ColumnInfo(name = "check_out_time") val checkOutTime: String? = null,
    @ColumnInfo(name = "check_in_lat") val checkInLat: Double? = null,
    @ColumnInfo(name = "check_in_lng") val checkInLng: Double? = null,
    @ColumnInfo(name = "check_in_accuracy") val checkInAccuracy: Float? = null,
    @ColumnInfo(name = "check_out_lat") val checkOutLat: Double? = null,
    @ColumnInfo(name = "check_out_lng") val checkOutLng: Double? = null,
    @ColumnInfo(name = "status") val status: String = "absent",
    @ColumnInfo(name = "device_risk_score") val deviceRiskScore: Int = 0,
    @ColumnInfo(name = "geo_fence_validated") val geoFenceValidated: Boolean = false,
    @ColumnInfo(name = "end_of_day_remarks") val endOfDayRemarks: String? = null,
    @ColumnInfo(name = "selfie_url") val selfieUrl: String? = null,
    @ColumnInfo(name = "check_out_selfie_url") val checkOutSelfieUrl: String? = null,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "pending",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)
