package com.tracko.app.data.remote.api

import com.tracko.app.data.remote.dto.ApiResponse
import com.tracko.app.data.remote.dto.TrackingBatchRequest
import com.tracko.app.data.remote.dto.TrackingPointDto
import com.tracko.app.data.remote.dto.TrackingStatsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TrackingApi {

    @POST("api/v1/tracking/batch")
    suspend fun uploadLocationBatch(@Body batch: TrackingBatchRequest): Response<ApiResponse<Unit>>

    @GET("api/v1/tracking/route")
    suspend fun getRoute(
        @Query("date") date: String,
        @Query("userId") userId: String? = null
    ): Response<ApiResponse<List<TrackingPointDto>>>

    @GET("api/v1/tracking/last-known")
    suspend fun getLastKnown(@Query("userId") userId: String? = null): Response<ApiResponse<TrackingPointDto>>

    @GET("api/v1/tracking/stats")
    suspend fun getTrackingStats(
        @Query("date") date: String,
        @Query("userId") userId: String? = null
    ): Response<ApiResponse<TrackingStatsResponse>>
}
