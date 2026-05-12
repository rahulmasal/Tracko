package com.tracko.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracking_logs")
data class TrackingLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "accuracy") val accuracy: Float? = null,
    @ColumnInfo(name = "speed") val speed: Float? = null,
    @ColumnInfo(name = "bearing") val bearing: Float? = null,
    @ColumnInfo(name = "altitude") val altitude: Double? = null,
    @ColumnInfo(name = "battery_level") val batteryLevel: Float? = null,
    @ColumnInfo(name = "is_mock") val isMock: Boolean = false,
    @ColumnInfo(name = "provider") val provider: String? = null,
    @ColumnInfo(name = "recorded_at") val recordedAt: Long,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "pending"
)
