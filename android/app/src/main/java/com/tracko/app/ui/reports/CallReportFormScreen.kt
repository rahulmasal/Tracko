package com.tracko.app.ui.reports

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tracko.app.ui.components.PhotoCaptureView
import com.tracko.app.ui.components.RatingBar
import com.tracko.app.ui.theme.AttendancePresent
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallReportFormScreen(
    visitId: Long? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val state by viewModel.formState
    val context = LocalContext.current

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onSaved()
    }

    val visitTypes = listOf("regular", "installation", "repair", "maintenance", "emergency", "survey", "demo")
    val resolutionStatuses = listOf("open", "in_progress", "resolved", "partial", "escalated", "closed")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Call Report") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Customer Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.customerName,
                onValueChange = { viewModel.updateFormField("customerName", it) },
                label = { Text("Customer Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.customerId,
                onValueChange = { viewModel.updateFormField("customerId", it) },
                label = { Text("Customer Code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.contactPerson,
                    onValueChange = { viewModel.updateFormField("contactPerson", it) },
                    label = { Text("Contact Person") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.contactNumber,
                    onValueChange = { viewModel.updateFormField("contactNumber", it) },
                    label = { Text("Contact Number") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Site Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.siteName,
                onValueChange = { viewModel.updateFormField("siteName", it) },
                label = { Text("Site Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.siteAddress,
                onValueChange = { viewModel.updateFormField("siteAddress", it) },
                label = { Text("Site Address") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Visit Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            var visitTypeExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = visitTypeExpanded,
                onExpandedChange = { visitTypeExpanded = it }
            ) {
                OutlinedTextField(
                    value = state.visitType.replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Visit Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = visitTypeExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = visitTypeExpanded,
                    onDismissRequest = { visitTypeExpanded = false }
                ) {
                    visitTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.replace("_", " ")) },
                            onClick = {
                                viewModel.updateFormField("visitType", type)
                                visitTypeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.timeSpentMinutes,
                onValueChange = { viewModel.updateFormField("timeSpentMinutes", it) },
                label = { Text("Time Spent (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Work Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.problemReported,
                onValueChange = { viewModel.updateFormField("problemReported", it) },
                label = { Text("Problem Reported") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.observation,
                onValueChange = { viewModel.updateFormField("observation", it) },
                label = { Text("Observation") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.workDone,
                onValueChange = { viewModel.updateFormField("workDone", it) },
                label = { Text("Work Done") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.partsUsed,
                onValueChange = { viewModel.updateFormField("partsUsed", it) },
                label = { Text("Parts Used") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Resolution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            var resolutionExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = resolutionExpanded,
                onExpandedChange = { resolutionExpanded = it }
            ) {
                OutlinedTextField(
                    value = state.resolutionStatus.replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Resolution Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = resolutionExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = resolutionExpanded,
                    onDismissRequest = { resolutionExpanded = false }
                ) {
                    resolutionStatuses.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.replace("_", " ")) },
                            onClick = {
                                viewModel.updateFormField("resolutionStatus", status)
                                resolutionExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.pendingIssue,
                onValueChange = { viewModel.updateFormField("pendingIssue", it) },
                label = { Text("Pending Issue") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.nextAction,
                onValueChange = { viewModel.updateFormField("nextAction", it) },
                label = { Text("Next Action") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.nextFollowupDate,
                onValueChange = { viewModel.updateFormField("nextFollowupDate", it) },
                label = { Text("Next Follow-up Date") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                viewModel.updateFormField(
                                    "nextFollowupDate",
                                    String.format("%04d-%02d-%02d", year, month + 1, day)
                                )
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Pick date")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Photos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            PhotoCaptureView(
                label = "Before Service Photos",
                imageUri = state.beforePhotos.lastOrNull(),
                onImageCaptured = { viewModel.addBeforePhoto(it) },
                onImageRemoved = { state.beforePhotos.lastOrNull()?.let { viewModel.removeBeforePhoto(it) } }
            )

            Spacer(modifier = Modifier.height(12.dp))

            PhotoCaptureView(
                label = "After Service Photos",
                imageUri = state.afterPhotos.lastOrNull(),
                onImageCaptured = { viewModel.addAfterPhoto(it) },
                onImageRemoved = { state.afterPhotos.lastOrNull()?.let { viewModel.removeAfterPhoto(it) } }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Customer Feedback",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.customerRemarks,
                onValueChange = { viewModel.updateFormField("customerRemarks", it) },
                label = { Text("Customer Remarks") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Customer Rating",
                style = MaterialTheme.typography.bodyMedium
            )
            RatingBar(
                rating = state.customerRating,
                onRatingChanged = { viewModel.updateCustomerRating(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Engineer Remarks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.engineerRemarks,
                onValueChange = { viewModel.updateFormField("engineerRemarks", it) },
                label = { Text("Engineer Remarks") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Signature area placeholder
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Customer Signature",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (state.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.saveDraft() },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isSaving && !state.isSubmitting
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Draft")
                    }
                }

                Button(
                    onClick = { viewModel.submitReport() },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isSubmitting && !state.isSaving
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Submit")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
