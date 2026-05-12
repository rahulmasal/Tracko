package com.tracko.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EnquiryDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("enquiryNumber") val enquiryNumber: String? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("customerId") val customerId: String? = null,
    @SerializedName("customerName") val customerName: String,
    @SerializedName("customerPhone") val customerPhone: String? = null,
    @SerializedName("customerEmail") val customerEmail: String? = null,
    @SerializedName("contactPerson") val contactPerson: String? = null,
    @SerializedName("contactNumber") val contactNumber: String? = null,
    @SerializedName("requirementDescription") val requirementDescription: String,
    @SerializedName("problemStatement") val problemStatement: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("priority") val priority: String? = null,
    @SerializedName("budgetRange") val budgetRange: String? = null,
    @SerializedName("source") val source: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("assignedTo") val assignedTo: String? = null,
    @SerializedName("assignedToName") val assignedToName: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("followups") val followups: List<FollowupDto>? = null,
    @SerializedName("closureNotes") val closureNotes: String? = null,
    @SerializedName("closureAmount") val closureAmount: Double? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class FollowupDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("notes") val notes: String,
    @SerializedName("followupBy") val followupBy: String? = null,
    @SerializedName("followupByName") val followupByName: String? = null,
    @SerializedName("followupAt") val followupAt: String? = null
)

data class UpdateEnquiryStatusRequest(
    @SerializedName("status") val status: String,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("closureAmount") val closureAmount: Double? = null
)
