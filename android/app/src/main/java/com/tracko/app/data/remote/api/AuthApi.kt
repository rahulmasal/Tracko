package com.tracko.app.data.remote.api

import com.tracko.app.data.remote.dto.ApiResponse
import com.tracko.app.data.remote.dto.LoginRequest
import com.tracko.app.data.remote.dto.LoginResponse
import com.tracko.app.data.remote.dto.OtpRequest
import com.tracko.app.data.remote.dto.OtpVerifyRequest
import com.tracko.app.data.remote.dto.RefreshTokenRequest
import com.tracko.app.data.remote.dto.ResetPasswordRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("api/v1/auth/login/otp")
    suspend fun loginWithOtp(@Body request: OtpRequest): Response<ApiResponse<LoginResponse>>

    @POST("api/v1/auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpVerifyRequest): Response<ApiResponse<LoginResponse>>

    @POST("api/v1/auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<ApiResponse<LoginResponse>>

    @POST("api/v1/auth/register-biometric")
    suspend fun registerBiometric(@Body request: Map<String, String>): Response<ApiResponse<Unit>>

    @POST("api/v1/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse<Unit>>

    @POST("api/v1/auth/bind-device")
    suspend fun bindDevice(@Body request: Map<String, String>): Response<ApiResponse<Unit>>

    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>
}
