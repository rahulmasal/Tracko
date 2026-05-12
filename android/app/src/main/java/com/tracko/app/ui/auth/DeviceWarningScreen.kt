package com.tracko.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tracko.app.security.SecurityCheckResult
import com.tracko.app.security.SecurityRiskLevel
import com.tracko.app.ui.theme.AttendancePresent
import com.tracko.app.ui.theme.ScoreAverage
import com.tracko.app.ui.theme.ScoreExcellent
import com.tracko.app.ui.theme.ScorePoor

@Composable
fun DeviceWarningScreen(
    onProceed: () -> Unit,
    onBlocked: () -> Unit,
    securityResult: SecurityCheckResult? = null
) {
    var proceedAllowed by remember { mutableStateOf(false) }

    val riskLevel = securityResult?.riskLevel ?: SecurityRiskLevel.LOW
    val riskScore = securityResult?.riskScore ?: 0

    val riskColor = when (riskLevel) {
        SecurityRiskLevel.LOW -> AttendancePresent
        SecurityRiskLevel.MEDIUM -> ScoreAverage
        SecurityRiskLevel.HIGH -> ScorePoor
        SecurityRiskLevel.CRITICAL -> Color(0xFF880E4F)
    }

    val riskLabel = when (riskLevel) {
        SecurityRiskLevel.LOW -> "Low Risk - Device is secure"
        SecurityRiskLevel.MEDIUM -> "Medium Risk - Some issues detected"
        SecurityRiskLevel.HIGH -> "High Risk - Several security concerns"
        SecurityRiskLevel.CRITICAL -> "Critical Risk - Device is not secure"
    }

    proceedAllowed = riskLevel != SecurityRiskLevel.CRITICAL

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Icon(
            imageVector = when {
                riskLevel == SecurityRiskLevel.LOW -> Icons.Default.CheckCircle
                riskLevel == SecurityRiskLevel.CRITICAL -> Icons.Default.Dangerous
                else -> Icons.Default.Warning
            },
            contentDescription = "Security status",
            modifier = Modifier.size(64.dp),
            tint = riskColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Device Security Check",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = riskLabel,
            style = MaterialTheme.typography.bodyLarge,
            color = riskColor,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { riskScore / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = riskColor,
            trackColor = riskColor.copy(alpha = 0.2f)
        )

        Text(
            text = "Risk Score: $riskScore/100",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Security Checks",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                securityResult?.checkDetails?.forEach { (check, failed) ->
                    SecurityCheckItem(
                        checkName = check,
                        isIssue = failed
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (riskLevel == SecurityRiskLevel.CRITICAL) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = ScorePoor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your device does not meet the security requirements. " +
                                "Please contact your system administrator for assistance.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBlocked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Return to Login")
            }
        } else if (riskLevel == SecurityRiskLevel.HIGH) {
            Button(
                onClick = onProceed,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Proceed with Restrictions")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Some features may be limited due to device security concerns",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        } else {
            Button(
                onClick = onProceed,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
private fun SecurityCheckItem(
    checkName: String,
    isIssue: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = if (isIssue) Icons.Default.Warning else Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (isIssue) ScorePoor else AttendancePresent,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = formatCheckName(checkName),
            style = MaterialTheme.typography.bodySmall,
            color = if (isIssue) ScorePoor else AttendancePresent
        )
    }
}

private fun formatCheckName(name: String): String {
    return when (name) {
        "rooted" -> "Device Root Status"
        "emulator" -> "Emulator Detection"
        "mockLocation" -> "Mock Location"
        "developerOptions" -> "Developer Options"
        "usbDebug" -> "USB Debugging"
        "tampered" -> "App Integrity"
        "playServicesMissing" -> "Google Play Services"
        "screenLockDisabled" -> "Screen Lock"
        else -> name
    }
}
