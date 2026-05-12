package com.tracko.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_reports")
data class CallReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "report_number") val reportNumber: String,
    @ColumnInfo(name = "visit_id") val visitId: Long? = null,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "customer_id") val customerId: String,
    @ColumnInfo(name = "customer_name") val customerName: String,
    @ColumnInfo(name = "contact_person") val contactPerson: String? = null,
    @ColumnInfo(name = "contact_number") val contactNumber: String? = null,
    @ColumnInfo(name = "site_name") val siteName: String? = null,
    @ColumnInfo(name = "site_address") val siteAddress: String? = null,
    @ColumnInfo(name = "visit_date") val visitDate: String,
    @ColumnInfo(name = "visit_type") val visitType: String,
    @ColumnInfo(name = "problem_reported") val problemReported: String? = null,
    @ColumnInfo(name = "observation") val observation: String? = null,
    @ColumnInfo(name = "work_done") val workDone: String? = null,
    @ColumnInfo(name = "parts_used") val partsUsed: String? = null,
    @ColumnInfo(name = "resolution_status") val resolutionStatus: String = "open",
    @ColumnInfo(name = "pending_issue") val pendingIssue: String? = null,
    @ColumnInfo(name = "next_action") val nextAction: String? = null,
    @ColumnInfo(name = "next_followup_date") val nextFollowupDate: String? = null,
    @ColumnInfo(name = "time_spent_minutes") val timeSpentMinutes: Int? = null,
    @ColumnInfo(name = "customer_remarks") val customerRemarks: String? = null,
    @ColumnInfo(name = "customer_rating") val customerRating: Int? = null,
    @ColumnInfo(name = "engineer_remarks") val engineerRemarks: String? = null,
    @ColumnInfo(name = "manager_remarks") val managerRemarks: String? = null,
    @ColumnInfo(name = "submission_status") val submissionStatus: String = "draft",
    @ColumnInfo(name = "signature_url") val signatureUrl: String? = null,
    @ColumnInfo(name = "photo_urls") val photoUrls: String? = null,
    @ColumnInfo(name = "pdf_url") val pdfUrl: String? = null,
    @ColumnInfo(name = "before_photos") val beforePhotos: String? = null,
    @ColumnInfo(name = "after_photos") val afterPhotos: String? = null,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "pending",
    @ColumnInfo(name = "server_id") val serverId: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)
