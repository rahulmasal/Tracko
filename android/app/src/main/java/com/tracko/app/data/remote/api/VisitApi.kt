package com.tracko.app.data.remote.api

import com.tracko.app.data.remote.dto.ApiResponse
import com.tracko.app.data.remote.dto.PaginatedResponse
import com.tracko.app.data.remote.dto.VisitDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface VisitApi {

    @GET("api/v1/visits/today")
    suspend fun getTodayVisits(): Response<ApiResponse<List<VisitDto>>>

    @GET("api/v1/visits/{id}")
    suspend fun getVisitById(@Path("id") visitId: Long): Response<ApiResponse<VisitDto>>

    @GET("api/v1/visits/history")
    suspend fun getVisitHistory(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("status") status: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<ApiResponse<PaginatedResponse<VisitDto>>>

    @POST("api/v1/visits/{id}/check-in")
    suspend fun checkInVisit(
        @Path("id") visitId: Long,
        @Body request: Map<String, Any>
    ): Response<ApiResponse<VisitDto>>

    @POST("api/v1/visits/{id}/check-out")
    suspend fun checkOutVisit(
        @Path("id") visitId: Long,
        @Body request: Map<String, Any>
    ): Response<ApiResponse<VisitDto>>

    @POST("api/v1/visits")
    suspend fun createVisit(@Body visit: VisitDto): Response<ApiResponse<VisitDto>>

    @PUT("api/v1/visits/{id}")
    suspend fun updateVisit(
        @Path("id") visitId: Long,
        @Body visit: VisitDto
    ): Response<ApiResponse<VisitDto>>
}
