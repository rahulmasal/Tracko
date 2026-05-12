package com.tracko.app.data.remote.api

import com.tracko.app.data.remote.dto.ApiResponse
import com.tracko.app.data.remote.dto.EnquiryDto
import com.tracko.app.data.remote.dto.PaginatedResponse
import com.tracko.app.data.remote.dto.UpdateEnquiryStatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface EnquiryApi {

    @POST("api/v1/enquiries")
    suspend fun createEnquiry(@Body enquiry: EnquiryDto): Response<ApiResponse<EnquiryDto>>

    @GET("api/v1/enquiries/{id}")
    suspend fun getEnquiry(@Path("id") enquiryId: Long): Response<ApiResponse<EnquiryDto>>

    @PUT("api/v1/enquiries/{id}")
    suspend fun updateEnquiry(
        @Path("id") enquiryId: Long,
        @Body enquiry: EnquiryDto
    ): Response<ApiResponse<EnquiryDto>>

    @POST("api/v1/enquiries/{id}/assign")
    suspend fun assignEnquiry(
        @Path("id") enquiryId: Long,
        @Body request: Map<String, String>
    ): Response<ApiResponse<EnquiryDto>>

    @PUT("api/v1/enquiries/{id}/status")
    suspend fun updateStatus(
        @Path("id") enquiryId: Long,
        @Body request: UpdateEnquiryStatusRequest
    ): Response<ApiResponse<EnquiryDto>>

    @POST("api/v1/enquiries/{id}/followup")
    suspend fun addFollowup(
        @Path("id") enquiryId: Long,
        @Body followup: Map<String, String>
    ): Response<ApiResponse<EnquiryDto>>

    @GET("api/v1/enquiries")
    suspend fun getEnquiryList(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("status") status: String? = null,
        @Query("priority") priority: String? = null,
        @Query("assignedTo") assignedTo: String? = null
    ): Response<ApiResponse<PaginatedResponse<EnquiryDto>>>
}
