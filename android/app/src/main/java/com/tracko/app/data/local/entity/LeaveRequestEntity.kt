package com.tracko.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leave_requests")
data class LeaveRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "leave_number") val leaveNumber: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "leave_type") val leaveType: String,
    @ColumnInfo(name = "start_date") val startDate: String,
    @ColumnInfo(name = "end_date") val endDate: String,
    @ColumnInfo(name = "total_days") val totalDays: Double,
    @ColumnInfo(name = "reason") val reason: String? = null,
    @ColumnInfo(name = "contact_during_leave") val contactDuringLeave: String? = null,
    @ColumnInfo(name = "attachment_url") val attachmentUrl: String? = null,
    @ColumnInfo(name = "status") val status: String = "pending",
    @ColumnInfo(name = "approved_by") val approvedBy: String? = null,
    @ColumnInfo(name = "approved_at") val approvedAt: String? = null,
    @ColumnInfo(name = "rejection_reason") val rejectionReason: String? = null,
    @ColumnInfo(name = "manager_remarks") val managerRemarks: String? = null,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "pending",
    @ColumnInfo(name = "server_id") val serverId: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)
