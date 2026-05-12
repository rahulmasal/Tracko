package com.tracko.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visits")
data class VisitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "visit_number") val visitNumber: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "customer_id") val customerId: String,
    @ColumnInfo(name = "customer_name") val customerName: String,
    @ColumnInfo(name = "site_address") val siteAddress: String,
    @ColumnInfo(name = "site_lat") val siteLat: Double? = null,
    @ColumnInfo(name = "site_lng") val siteLng: Double? = null,
    @ColumnInfo(name = "planned_date") val plannedDate: String,
    @ColumnInfo(name = "planned_start") val plannedStart: String? = null,
    @ColumnInfo(name = "planned_end") val plannedEnd: String? = null,
    @ColumnInfo(name = "actual_check_in") val actualCheckIn: String? = null,
    @ColumnInfo(name = "actual_check_out") val actualCheckOut: String? = null,
    @ColumnInfo(name = "visit_type") val visitType: String = "regular",
    @ColumnInfo(name = "priority") val priority: String = "normal",
    @ColumnInfo(name = "status") val status: String = "planned",
    @ColumnInfo(name = "ticket_number") val ticketNumber: String? = null,
    @ColumnInfo(name = "time_on_site_minutes") val timeOnSiteMinutes: Int? = null,
    @ColumnInfo(name = "customer_phone") val customerPhone: String? = null,
    @ColumnInfo(name = "remarks") val remarks: String? = null,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "pending",
    @ColumnInfo(name = "server_id") val serverId: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)
