package com.tracko.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tracko.app.ui.theme.SyncError
import com.tracko.app.ui.theme.SyncPending
import com.tracko.app.ui.theme.SyncSynced
import com.tracko.app.ui.theme.SyncSyncing
import com.tracko.app.util.SyncState
import com.tracko.app.util.SyncStatus

@Composable
fun SyncStatusIndicator(
    syncState: SyncState,
    modifier: Modifier = Modifier
) {
    val (icon, color, label) = when (syncState.status) {
        SyncStatus.IDLE -> Icons.Default.CloudDone to SyncSynced to "All synced"
        SyncStatus.SYNCING -> Icons.Default.CloudSync to SyncSyncing to "Syncing..."
        SyncStatus.PENDING -> Icons.Default.CloudSync to SyncPending to "${syncState.pendingCount} pending"
        SyncStatus.ERROR -> Icons.Default.SyncProblem to SyncError to "Sync error"
    }

    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Sync status",
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
        if (syncState.lastSyncTime != null && syncState.status == SyncStatus.IDLE) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = com.tracko.app.util.DateTimeUtils.getTimeAgo(syncState.lastSyncTime),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
