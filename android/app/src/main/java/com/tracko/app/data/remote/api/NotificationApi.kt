package com.tracko.app.data.remote.api

import com.tracko.app.data.remote.dto.ApiResponse
import com.tracko.app.data.remote.dto.NotificationDto
import com.tracko.app.data.remote.dto.PaginatedResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApi {

    @GET("api/v1/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PaginatedResponse<NotificationDto>>>

    @PUT("api/v1/notifications/{id}/read")
    suspend fun markRead(@Path("id") notificationId: String): Response<ApiResponse<Unit>>

    @PUT("api/v1/notifications/read-all")
    suspend fun markAllRead(): Response<ApiResponse<Unit>>

    @GET("api/v1/notifications/unread-count")
    suspend fun getUnreadCount(): Response<ApiResponse<Int>>

    @POST("api/v1/notifications/register-device")
    suspend fun registerDevice(@Body request: Map<String, String>): Response<ApiResponse<Unit>>
}
