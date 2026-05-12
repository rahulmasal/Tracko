package com.tracko.app.ui.visits

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tracko.app.data.local.entity.VisitEntity
import com.tracko.app.ui.components.LoadingIndicator
import com.tracko.app.ui.components.PriorityBadge
import com.tracko.app.ui.components.VisitStatusBadge
import com.tracko.app.ui.theme.AttendancePresent
import com.tracko.app.ui.theme.ScorePoor
import com.tracko.app.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitDetailScreen(
    visitId: Long,
    onBack: () -> Unit,
    onCreateReport: (Long) -> Unit,
    onNavigateToMaps: (Double, Double) -> Unit,
    viewModel: VisitViewModel = hiltViewModel()
) {
    val state by viewModel.detailState

    LaunchedEffect(visitId) {
        viewModel.loadVisitDetail(visitId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visit Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            val visit = state.visit
            if (visit == null) {
                Text(
                    "Visit not found",
                    modifier = Modifier.padding(padding),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = visit.customerName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    VisitStatusBadge(status = visit.status)
                                    PriorityBadge(priority = visit.priority)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (visit.ticketNumber != null) {
                                Text(
                                    text = "Ticket #${visit.ticketNumber}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(12.dp))

                            DetailRow(Icons.Default.Person, "Customer Code", visit.customerId)
                            DetailRow(Icons.Default.Phone, "Phone", visit.customerPhone ?: "N/A")
                            DetailRow(Icons.Default.LocationOn, "Address", visit.siteAddress)
                            DetailRow(Icons.Default.CalendarMonth, "Date", DateTimeUtils.formatDate(visit.plannedDate))
                            DetailRow(Icons.Default.Schedule, "Time", "${visit.plannedStart ?: "N/A"} - ${visit.plannedEnd ?: "N/A"}")
                            DetailRow(Icons.Default.Description, "Visit Type", visit.visitType.replace("_", " "))

                            if (visit.actualCheckIn != null) {
                                DetailRow(
                                    Icons.Default.CheckCircle,
                                    "Actual Check-in",
                                    DateTimeUtils.formatDateTime(visit.actualCheckIn),
                                    AttendancePresent
                                )
                            }
                            if (visit.actualCheckOut != null) {
                                DetailRow(
                                    Icons.Default.CheckCircle,
                                    "Actual Check-out",
                                    DateTimeUtils.formatDateTime(visit.actualCheckOut),
                                    AttendancePresent
                                )
                            }
                            if (visit.timeOnSiteMinutes != null) {
                                DetailRow(
                                    Icons.Default.Schedule,
                                    "Time on Site",
                                    DateTimeUtils.formatDuration(visit.timeOnSiteMinutes)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (visit.remarks != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Remarks",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = visit.remarks ?: "",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (visit.siteLat != null && visit.siteLng != null) {
                        OutlinedButton(
                            onClick = { onNavigateToMaps(visit.siteLat, visit.siteLng) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Navigate in Maps")
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    when (visit.status) {
                        "planned" -> {
                            Button(
                                onClick = {
                                    viewModel.checkInVisit(visit.id, 0.0, 0.0)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.isCheckingIn
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Check In to Visit")
                            }
                        }
                        "in_progress" -> {
                            Button(
                                onClick = {
                                    viewModel.checkOutVisit(visit.id, 0.0, 0.0)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.isCheckingOut,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ScorePoor
                                )
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Check Out from Visit")
                            }
                        }
                        "completed" -> {
                            Button(
                                onClick = { onCreateReport(visit.id) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Create / Edit Report")
                            }
                        }
                    }

                    if (state.successMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.successMessage ?: "",
                            color = AttendancePresent,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (state.error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String, valueColor: Color? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor ?: MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
