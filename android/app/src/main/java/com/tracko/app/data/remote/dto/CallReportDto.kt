package com.tracko.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CallReportDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("reportNumber") val reportNumber: String? = null,
    @SerializedName("visitId") val visitId: Long? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("customerName") val customerName: String,
    @SerializedName("contactPerson") val contactPerson: String? = null,
    @SerializedName("contactNumber") val contactNumber: String? = null,
    @SerializedName("siteName") val siteName: String? = null,
    @SerializedName("siteAddress") val siteAddress: String? = null,
    @SerializedName("visitDate") val visitDate: String,
    @SerializedName("visitType") val visitType: String,
    @SerializedName("problemReported") val problemReported: String? = null,
    @SerializedName("observation") val observation: String? = null,
    @SerializedName("workDone") val workDone: String? = null,
    @SerializedName("partsUsed") val partsUsed: String? = null,
    @SerializedName("resolutionStatus") val resolutionStatus: String? = null,
    @SerializedName("pendingIssue") val pendingIssue: String? = null,
    @SerializedName("nextAction") val nextAction: String? = null,
    @SerializedName("nextFollowupDate") val nextFollowupDate: String? = null,
    @SerializedName("timeSpentMinutes") val timeSpentMinutes: Int? = null,
    @SerializedName("customerRemarks") val customerRemarks: String? = null,
    @SerializedName("customerRating") val customerRating: Int? = null,
    @SerializedName("engineerRemarks") val engineerRemarks: String? = null,
    @SerializedName("managerRemarks") val managerRemarks: String? = null,
    @SerializedName("submissionStatus") val submissionStatus: String? = null,
    @SerializedName("signatureUrl") val signatureUrl: String? = null,
    @SerializedName("photoUrls") val photoUrls: List<String>? = null,
    @SerializedName("pdfUrl") val pdfUrl: String? = null
)
