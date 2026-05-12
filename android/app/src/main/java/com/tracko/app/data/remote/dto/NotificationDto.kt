package com.tracko.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NotificationDto(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("type") val type: String,
    @SerializedName("referenceId") val referenceId: String? = null,
    @SerializedName("referenceType") val referenceType: String? = null,
    @SerializedName("isRead") val isRead: Boolean = false,
    @SerializedName("priority") val priority: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("actionUrl") val actionUrl: String? = null,
    @SerializedName("receivedAt") val receivedAt: String
)
