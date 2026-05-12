package com.tracko.app.data.remote.interceptor

import com.tracko.app.data.remote.dto.LoginResponse
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor, Authenticator {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { tokenManager.getAccessToken() }

        val request = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(request)

        if (response.code == 401) {
            synchronized(this) {
                val newToken = runBlocking { tokenManager.getAccessToken() }
                if (newToken != token) {
                    val retryRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                    response.close()
                    return chain.proceed(retryRequest)
                }
            }
        }

        return response
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken == null) {
                tokenManager.clearTokens()
                return@runBlocking null
            }

            try {
                val client = OkHttpClient.Builder().build()
                val refreshBody = Gson().toJson(mapOf("refreshToken" to refreshToken))
                val refreshRequest = Request.Builder()
                    .url(response.request.url.toString().substringBefore("/api") + "/api/v1/auth/refresh-token")
                    .header("Content-Type", "application/json")
                    .post(okhttp3.RequestBody.create(
                        okhttp3.MediaType.parse("application/json"), refreshBody
                    ))
                    .build()

                val refreshResponse = client.newCall(refreshRequest).execute()
                if (refreshResponse.isSuccessful) {
                    val body = refreshResponse.body()?.string()
                    val loginResponse = Gson().fromJson(body, LoginResponse::class.java)
                    tokenManager.saveTokens(
                        loginResponse.accessToken,
                        loginResponse.refreshToken
                    )
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${loginResponse.accessToken}")
                        .build()
                } else {
                    tokenManager.clearTokens()
                    null
                }
            } catch (e: Exception) {
                tokenManager.clearTokens()
                null
            }
        }
    }
}

interface TokenManager {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
    suspend fun getUserId(): String?
    suspend fun saveUserData(loginResponse: LoginResponse)
    suspend fun isLoggedIn(): Boolean
}
