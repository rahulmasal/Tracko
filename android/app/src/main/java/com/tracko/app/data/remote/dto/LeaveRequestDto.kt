package com.tracko.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LeaveRequestDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("leaveNumber") val leaveNumber: String? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("leaveType") val leaveType: String,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("totalDays") val totalDays: Double,
    @SerializedName("reason") val reason: String? = null,
    @SerializedName("contactDuringLeave") val contactDuringLeave: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("approvedBy") val approvedBy: String? = null,
    @SerializedName("approvedByName") val approvedByName: String? = null,
    @SerializedName("approvedAt") val approvedAt: String? = null,
    @SerializedName("rejectionReason") val rejectionReason: String? = null,
    @SerializedName("managerRemarks") val managerRemarks: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class LeaveBalanceResponse(
    @SerializedName("annual") val annual: LeaveBalanceItem? = null,
    @SerializedName("sick") val sick: LeaveBalanceItem? = null,
    @SerializedName("personal") val personal: LeaveBalanceItem? = null,
    @SerializedName("casual") val casual: LeaveBalanceItem? = null,
    @SerializedName("other") val other: LeaveBalanceItem? = null
)

data class LeaveBalanceItem(
    @SerializedName("total") val total: Double,
    @SerializedName("used") val used: Double,
    @SerializedName("remaining") val remaining: Double,
    @SerializedName("pending") val pending: Double = 0.0
)
