package com.tracko.app.security

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deviceSecurityChecker: DeviceSecurityChecker
) {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private val keyAlias = "tracko_security_key"

    init {
        if (!keyStore.containsAlias(keyAlias)) {
            generateKey()
        }
    }

    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(spec)
        keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey? {
        return keyStore.getEntry(keyAlias, null)?.let { (it as KeyStore.SecretKeyEntry).secretKey }
    }

    fun encryptData(plainText: String): ByteArray? {
        return try {
            val secretKey = getSecretKey() ?: return null
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        } catch (e: Exception) {
            null
        }
    }

    fun decryptData(encryptedData: ByteArray, iv: ByteArray): String? {
        return try {
            val secretKey = getSecretKey() ?: return null
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            String(cipher.doFinal(encryptedData), Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    fun checkDeviceSecurity(): SecurityCheckResult {
        return deviceSecurityChecker.performSecurityCheck()
    }

    fun isDeviceAllowed(): Boolean {
        val result = checkDeviceSecurity()
        return result.riskLevel != SecurityRiskLevel.CRITICAL
    }

    fun getAllowedActions(result: SecurityCheckResult): List<String> {
        return when (result.riskLevel) {
            SecurityRiskLevel.LOW -> listOf(
                "check_in", "check_out", "visit", "report", "enquiry",
                "leave_apply", "tracking", "photo_capture", "all"
            )
            SecurityRiskLevel.MEDIUM -> listOf(
                "check_in", "check_out", "visit", "report",
                "leave_apply", "tracking"
            )
            SecurityRiskLevel.HIGH -> listOf(
                "check_in", "check_out", "visit", "leave_apply"
            )
            SecurityRiskLevel.CRITICAL -> emptyList()
        }
    }
}
