package com.tracko.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VisitDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("visitNumber") val visitNumber: String? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("customerName") val customerName: String,
    @SerializedName("customerPhone") val customerPhone: String? = null,
    @SerializedName("siteAddress") val siteAddress: String,
    @SerializedName("siteLat") val siteLat: Double? = null,
    @SerializedName("siteLng") val siteLng: Double? = null,
    @SerializedName("plannedDate") val plannedDate: String,
    @SerializedName("plannedStart") val plannedStart: String? = null,
    @SerializedName("plannedEnd") val plannedEnd: String? = null,
    @SerializedName("actualCheckIn") val actualCheckIn: String? = null,
    @SerializedName("actualCheckOut") val actualCheckOut: String? = null,
    @SerializedName("visitType") val visitType: String? = null,
    @SerializedName("priority") val priority: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("ticketNumber") val ticketNumber: String? = null,
    @SerializedName("timeOnSiteMinutes") val timeOnSiteMinutes: Int? = null,
    @SerializedName("remarks") val remarks: String? = null
)
