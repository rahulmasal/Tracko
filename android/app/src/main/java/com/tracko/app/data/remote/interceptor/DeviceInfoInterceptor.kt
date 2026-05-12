package com.tracko.app.data.remote.interceptor

import android.os.Build
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceInfoInterceptor @Inject constructor(
    private val appVersionProvider: AppVersionProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val request = originalRequest.newBuilder()
            .header("X-App-Version", appVersionProvider.getVersion())
            .header("X-OS-Version", Build.VERSION.RELEASE)
            .header("X-Device-Model", "${Build.MANUFACTURER} ${Build.MODEL}")
            .header("X-Device-Fingerprint", Build.FINGERPRINT)
            .header("X-Android-SDK", Build.VERSION.SDK_INT.toString())
            .header("X-Platform", "android")
            .build()

        return chain.proceed(request)
    }
}

interface AppVersionProvider {
    fun getVersion(): String
}
