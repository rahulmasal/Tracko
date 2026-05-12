package com.tracko.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("deviceId") val deviceId: String? = null,
    @SerializedName("fcmToken") val fcmToken: String? = null
)

data class OtpRequest(
    @SerializedName("mobile") val mobile: String,
    @SerializedName("deviceId") val deviceId: String? = null
)

data class OtpVerifyRequest(
    @SerializedName("mobile") val mobile: String,
    @SerializedName("otp") val otp: String,
    @SerializedName("deviceId") val deviceId: String? = null
)

data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

data class ResetPasswordRequest(
    @SerializedName("oldPassword") val oldPassword: String? = null,
    @SerializedName("newPassword") val newPassword: String,
    @SerializedName("confirmPassword") val confirmPassword: String,
    @SerializedName("otp") val otp: String? = null
)

data class LoginResponse(
    @SerializedName("userId") val userId: String,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("tokenType") val tokenType: String = "Bearer",
    @SerializedName("expiresIn") val expiresIn: Long,
    @SerializedName("employeeName") val employeeName: String? = null,
    @SerializedName("employeeCode") val employeeCode: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("designation") val designation: String? = null,
    @SerializedName("department") val department: String? = null,
    @SerializedName("profileImageUrl") val profileImageUrl: String? = null,
    @SerializedName("mobile") val mobile: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("requiresBiometric") val requiresBiometric: Boolean = false,
    @SerializedName("requiresPasswordChange") val requiresPasswordChange: Boolean = false
)
