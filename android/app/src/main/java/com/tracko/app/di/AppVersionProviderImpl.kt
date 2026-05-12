package com.tracko.app.di

import android.content.Context
import android.content.pm.PackageManager
import com.tracko.app.data.remote.interceptor.AppVersionProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppVersionProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AppVersionProvider {

    override fun getVersion(): String {
        return try {
            val pkgInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pkgInfo.versionName ?: "1.0.0"
        } catch (e: PackageManager.NameNotFoundException) {
            "1.0.0"
        }
    }
}
