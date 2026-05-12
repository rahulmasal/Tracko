package com.tracko.app.data.remote.api

import com.tracko.app.data.remote.dto.ApiResponse
import com.tracko.app.data.remote.dto.AttendanceRequest
import com.tracko.app.data.remote.dto.AttendanceResponse
import com.tracko.app.data.remote.dto.PaginatedResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AttendanceApi {

    @POST("api/v1/attendance/check-in")
    suspend fun checkIn(@Body request: AttendanceRequest): Response<ApiResponse<AttendanceResponse>>

    @POST("api/v1/attendance/check-out")
    suspend fun checkOut(@Body request: AttendanceRequest): Response<ApiResponse<AttendanceResponse>>

    @GET("api/v1/attendance/today")
    suspend fun getTodayAttendance(): Response<ApiResponse<AttendanceResponse>>

    @GET("api/v1/attendance/history")
    suspend fun getAttendanceHistory(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<ApiResponse<PaginatedResponse<AttendanceResponse>>>

    @GET("api/v1/attendance/team")
    suspend fun getTeamAttendance(
        @Query("date") date: String
    ): Response<ApiResponse<List<AttendanceResponse>>>

    @POST("api/v1/attendance/{id}/correction")
    suspend fun requestCorrection(
        @Path("id") attendanceId: Long,
        @Body request: Map<String, String>
    ): Response<ApiResponse<Unit>>

    @PUT("api/v1/attendance/correction/{id}/review")
    suspend fun reviewCorrection(
        @Path("id") correctionId: Long,
        @Body request: Map<String, String>
    ): Response<ApiResponse<Unit>>
}
