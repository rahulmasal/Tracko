package com.tracko.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "enquiries")
data class EnquiryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "enquiry_number") val enquiryNumber: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "customer_id") val customerId: String? = null,
    @ColumnInfo(name = "customer_name") val customerName: String,
    @ColumnInfo(name = "customer_phone") val customerPhone: String? = null,
    @ColumnInfo(name = "customer_email") val customerEmail: String? = null,
    @ColumnInfo(name = "contact_person") val contactPerson: String? = null,
    @ColumnInfo(name = "contact_number") val contactNumber: String? = null,
    @ColumnInfo(name = "requirement_description") val requirementDescription: String,
    @ColumnInfo(name = "problem_statement") val problemStatement: String? = null,
    @ColumnInfo(name = "category") val category: String? = null,
    @ColumnInfo(name = "priority") val priority: String = "medium",
    @ColumnInfo(name = "budget_range") val budgetRange: String? = null,
    @ColumnInfo(name = "source") val source: String? = null,
    @ColumnInfo(name = "status") val status: String = "new",
    @ColumnInfo(name = "assigned_to") val assignedTo: String? = null,
    @ColumnInfo(name = "assigned_at") val assignedAt: String? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "photo_urls") val photoUrls: String? = null,
    @ColumnInfo(name = "followup_data") val followupData: String? = null,
    @ColumnInfo(name = "closure_notes") val closureNotes: String? = null,
    @ColumnInfo(name = "closure_amount") val closureAmount: Double? = null,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "pending",
    @ColumnInfo(name = "server_id") val serverId: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)
