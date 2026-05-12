package com.tracko.app.data.remote.api

import com.tracko.app.data.remote.dto.ApiResponse
import com.tracko.app.data.remote.dto.LeaveBalanceResponse
import com.tracko.app.data.remote.dto.LeaveRequestDto
import com.tracko.app.data.remote.dto.PaginatedResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface LeaveApi {

    @POST("api/v1/leaves")
    suspend fun applyLeave(@Body request: LeaveRequestDto): Response<ApiResponse<LeaveRequestDto>>

    @GET("api/v1/leaves/{id}")
    suspend fun getLeave(@Path("id") leaveId: Long): Response<ApiResponse<LeaveRequestDto>>

    @POST("api/v1/leaves/{id}/cancel")
    suspend fun cancelLeave(@Path("id") leaveId: Long): Response<ApiResponse<LeaveRequestDto>>

    @PUT("api/v1/leaves/{id}/approve")
    suspend fun approveLeave(
        @Path("id") leaveId: Long,
        @Body remarks: Map<String, String>? = null
    ): Response<ApiResponse<LeaveRequestDto>>

    @PUT("api/v1/leaves/{id}/reject")
    suspend fun rejectLeave(
        @Path("id") leaveId: Long,
        @Body request: Map<String, String>
    ): Response<ApiResponse<LeaveRequestDto>>

    @GET("api/v1/leaves/history")
    suspend fun getLeaveHistory(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("status") status: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<ApiResponse<PaginatedResponse<LeaveRequestDto>>>

    @GET("api/v1/leaves/balance")
    suspend fun getLeaveBalance(): Response<ApiResponse<LeaveBalanceResponse>>

    @GET("api/v1/leaves/pending")
    suspend fun getPendingLeaves(): Response<ApiResponse<List<LeaveRequestDto>>>
}
