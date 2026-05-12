package com.tracko.app.security

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class SecurityCheckResult(
    val riskScore: Int,
    val riskLevel: SecurityRiskLevel,
    val isRooted: Boolean = false,
    val isEmulator: Boolean = false,
    val isMockLocationEnabled: Boolean = false,
    val isDeveloperOptionsOn: Boolean = false,
    val isUsbDebuggingOn: Boolean = false,
    val isAppTampered: Boolean = false,
    val isGooglePlayServicesMissing: Boolean = false,
    val isScreenLockDisabled: Boolean = false,
    val checkDetails: Map<String, Boolean> = emptyMap()
)

enum class SecurityRiskLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}

@Singleton
class DeviceSecurityChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun performSecurityCheck(): SecurityCheckResult {
        val checks = mutableMapOf<String, Boolean>()

        val rooted = isRooted().also { checks["rooted"] = it }
        val emulator = isEmulator().also { checks["emulator"] = it }
        val mockLocation = isMockLocationEnabled().also { checks["mockLocation"] = it }
        val developerOptions = isDeveloperOptionsOn().also { checks["developerOptions"] = it }
        val usbDebug = isUsbDebuggingOn().also { checks["usbDebug"] = it }
        val tampered = isAppTampered().also { checks["tampered"] = it }
        val playServicesMissing = isGooglePlayServicesMissing().also { checks["playServicesMissing"] = it }
        val screenLockDisabled = isScreenLockDisabled().also { checks["screenLockDisabled"] = it }

        val totalChecks = checks.size
        val failedChecks = checks.count { it.value }
        val riskScore = calculateRiskScore(
            rooted, emulator, mockLocation, developerOptions,
            usbDebug, tampered, playServicesMissing, screenLockDisabled
        )

        val riskLevel = when {
            riskScore > 75 -> SecurityRiskLevel.CRITICAL
            riskScore > 50 -> SecurityRiskLevel.HIGH
            riskScore > 25 -> SecurityRiskLevel.MEDIUM
            else -> SecurityRiskLevel.LOW
        }

        return SecurityCheckResult(
            riskScore = riskScore,
            riskLevel = riskLevel,
            isRooted = rooted,
            isEmulator = emulator,
            isMockLocationEnabled = mockLocation,
            isDeveloperOptionsOn = developerOptions,
            isUsbDebuggingOn = usbDebug,
            isAppTampered = tampered,
            isGooglePlayServicesMissing = playServicesMissing,
            isScreenLockDisabled = screenLockDisabled,
            checkDetails = checks
        )
    }

    private fun isRooted(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/system/bin/su",
            "/system/xbin/su",
            "/system/framework/core.jar",
            "/sbin/su",
            "/system/etc/init.d/99SuperSUDaemon",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            val reader = BufferedReader(java.io.InputStreamReader(process.inputStream))
            reader.readLine() != null
        } catch (_: Exception) {
            false
        }
    }

    private fun isEmulator(): Boolean {
        val fingerprints = arrayOf(
            "google/sdk_gphone", "generic", "generic_x86", "generic_x86_64",
            "generic_arm64", "sdk", "sdk_gphone", "vbox86p", "C4CA4238A0B9"
        )
        val brand = Build.BRAND.lowercase()
        val manufacturer = Build.MANUFACTURER.lowercase()
        val product = Build.PRODUCT.lowercase()
        val device = Build.DEVICE.lowercase()
        val model = Build.MODEL.lowercase()
        val fingerprint = Build.FINGERPRINT.lowercase()

        return fingerprints.any {
            brand.contains(it) || manufacturer.contains(it) ||
                    product.contains(it) || device.contains(it) ||
                    model.contains(it) || fingerprint.contains(it)
        } || (Build.HARDWARE == "goldfish" || Build.HARDWARE == "ranchu")
    }

    private fun isMockLocationEnabled(): Boolean {
        return try {
            !Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ALLOW_MOCK_LOCATION
            ).isNullOrEmpty()
        } catch (_: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                try {
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    false
                } catch (_: SecurityException) {
                    false
                }
            } else false
        }
    }

    private fun isDeveloperOptionsOn(): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED
            ) != 0
        } catch (_: Exception) {
            false
        }
    }

    private fun isUsbDebuggingOn(): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED
            ) != 0
        } catch (_: Exception) {
            false
        }
    }

    private fun isAppTampered(): Boolean {
        val appSignature = try {
            val pkgInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
            }
            pkgInfo?.signingInfo?.apkContentsSigners?.firstOrNull()
        } catch (_: Exception) { null }
        return appSignature == null
    }

    private fun isGooglePlayServicesMissing(): Boolean {
        return try {
            val pkgInfo = context.packageManager.getPackageInfo(
                "com.google.android.gms",
                PackageManager.GET_ACTIVITIES
            )
            pkgInfo == null
        } catch (_: Exception) {
            true
        }
    }

    private fun isScreenLockDisabled(): Boolean {
        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        return try {
            val activeAdmins = devicePolicyManager.activeAdmins
            val passwordQuality = devicePolicyManager.getPasswordQuality(null)
            passwordQuality == DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED
        } catch (_: Exception) {
            true
        }
    }

    private fun calculateRiskScore(
        rooted: Boolean, emulator: Boolean, mockLocation: Boolean,
        developerOptions: Boolean, usbDebug: Boolean, tampered: Boolean,
        playServicesMissing: Boolean, screenLockDisabled: Boolean
    ): Int {
        var score = 0
        if (rooted) score += 30
        if (emulator) score += 25
        if (mockLocation) score += 20
        if (developerOptions) score += 10
        if (usbDebug) score += 10
        if (tampered) score += 30
        if (playServicesMissing) score += 15
        if (screenLockDisabled) score += 5
        return score.coerceIn(0, 100)
    }
}
