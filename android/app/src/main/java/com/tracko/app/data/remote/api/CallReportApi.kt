package com.tracko.app.data.remote.api

import com.tracko.app.data.remote.dto.ApiResponse
import com.tracko.app.data.remote.dto.CallReportDto
import com.tracko.app.data.remote.dto.PaginatedResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface CallReportApi {

    @POST("api/v1/reports")
    suspend fun createReport(@Body report: CallReportDto): Response<ApiResponse<CallReportDto>>

    @PUT("api/v1/reports/{id}")
    suspend fun updateReport(
        @Path("id") reportId: Long,
        @Body report: CallReportDto
    ): Response<ApiResponse<CallReportDto>>

    @POST("api/v1/reports/{id}/submit")
    suspend fun submitReport(@Path("id") reportId: Long): Response<ApiResponse<CallReportDto>>

    @PUT("api/v1/reports/{id}/review")
    suspend fun reviewReport(
        @Path("id") reportId: Long,
        @Body review: Map<String, String>
    ): Response<ApiResponse<CallReportDto>>

    @GET("api/v1/reports/{id}")
    suspend fun getReport(@Path("id") reportId: Long): Response<ApiResponse<CallReportDto>>

    @GET("api/v1/reports/history")
    suspend fun getReportHistory(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("status") status: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("customerId") customerId: String? = null
    ): Response<ApiResponse<PaginatedResponse<CallReportDto>>>

    @GET("api/v1/reports/pending")
    suspend fun getPendingReports(): Response<ApiResponse<List<CallReportDto>>>

    @Multipart
    @POST("api/v1/reports/upload-photo")
    suspend fun uploadPhoto(
        @Part photo: MultipartBody.Part,
        @Part("reportId") reportId: RequestBody,
        @Part("type") type: RequestBody
    ): Response<ApiResponse<String>>
}
