package com.tracko.app.data.repository

import com.tracko.app.data.remote.api.AuthApi
import com.tracko.app.data.remote.dto.LoginRequest
import com.tracko.app.data.remote.dto.LoginResponse
import com.tracko.app.data.remote.dto.OtpRequest
import com.tracko.app.data.remote.dto.OtpVerifyRequest
import com.tracko.app.data.remote.dto.RefreshTokenRequest
import com.tracko.app.data.remote.dto.ResetPasswordRequest
import com.tracko.app.data.remote.interceptor.TokenManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    data object Loading : AuthResult()
    data class Success(val loginResponse: LoginResponse) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {
    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Loading)
    val authState: StateFlow<AuthResult> = _authState.asStateFlow()

    suspend fun login(username: String, password: String, deviceId: String? = null, fcmToken: String? = null) {
        _authState.value = AuthResult.Loading
        try {
            val response = authApi.login(
                LoginRequest(
                    username = username,
                    password = password,
                    deviceId = deviceId,
                    fcmToken = fcmToken
                )
            )
            if (response.isSuccessful && response.body()?.success == true) {
                val loginResponse = response.body()!!.data!!
                tokenManager.saveTokens(loginResponse.accessToken, loginResponse.refreshToken)
                tokenManager.saveUserData(loginResponse)
                _authState.value = AuthResult.Success(loginResponse)
            } else {
                val errorMsg = response.body()?.message ?: response.errorBody()?.string() ?: "Login failed"
                _authState.value = AuthResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            _authState.value = AuthResult.Error(e.localizedMessage ?: "Network error occurred")
        }
    }

    suspend fun loginWithOtp(mobile: String, deviceId: String? = null) {
        _authState.value = AuthResult.Loading
        try {
            val response = authApi.loginWithOtp(OtpRequest(mobile, deviceId))
            if (response.isSuccessful && response.body()?.success == true) {
                val loginResponse = response.body()!!.data!!
                tokenManager.saveTokens(loginResponse.accessToken, loginResponse.refreshToken)
                tokenManager.saveUserData(loginResponse)
                _authState.value = AuthResult.Success(loginResponse)
            } else {
                val errorMsg = response.body()?.message ?: response.errorBody()?.string() ?: "OTP login failed"
                _authState.value = AuthResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            _authState.value = AuthResult.Error(e.localizedMessage ?: "Network error occurred")
        }
    }

    suspend fun verifyOtp(mobile: String, otp: String, deviceId: String? = null) {
        _authState.value = AuthResult.Loading
        try {
            val response = authApi.verifyOtp(OtpVerifyRequest(mobile, otp, deviceId))
            if (response.isSuccessful && response.body()?.success == true) {
                val loginResponse = response.body()!!.data!!
                tokenManager.saveTokens(loginResponse.accessToken, loginResponse.refreshToken)
                tokenManager.saveUserData(loginResponse)
                _authState.value = AuthResult.Success(loginResponse)
            } else {
                val errorMsg = response.body()?.message ?: response.errorBody()?.string() ?: "OTP verification failed"
                _authState.value = AuthResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            _authState.value = AuthResult.Error(e.localizedMessage ?: "Network error occurred")
        }
    }

    suspend fun refreshToken(): Boolean {
        return try {
            val refreshToken = tokenManager.getRefreshToken() ?: return false
            val response = authApi.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.isSuccessful && response.body()?.success == true) {
                val loginResponse = response.body()!!.data!!
                tokenManager.saveTokens(loginResponse.accessToken, loginResponse.refreshToken)
                true
            } else {
                tokenManager.clearTokens()
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun resetPassword(oldPassword: String?, newPassword: String, confirmPassword: String, otp: String? = null): Result<Unit> {
        return try {
            val response = authApi.resetPassword(
                ResetPasswordRequest(oldPassword, newPassword, confirmPassword, otp)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Password reset failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerBiometric(biometricKey: String): Result<Unit> {
        return try {
            val response = authApi.registerBiometric(
                mapOf("biometricKey" to biometricKey, "deviceId" to android.os.Build.SERIAL)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Biometric registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        try {
            authApi.logout()
        } catch (_: Exception) { }
        tokenManager.clearTokens()
    }

    suspend fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    suspend fun getUserId(): String? = tokenManager.getUserId()

    suspend fun saveUserData(loginResponse: LoginResponse) = tokenManager.saveUserData(loginResponse)
}
