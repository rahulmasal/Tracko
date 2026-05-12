package com.tracko.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tracko.app.ui.theme.AttendanceAbsent
import com.tracko.app.ui.theme.AttendanceLate
import com.tracko.app.ui.theme.AttendancePresent
import com.tracko.app.ui.theme.PriorityHigh
import com.tracko.app.ui.theme.PriorityLow
import com.tracko.app.ui.theme.PriorityMedium
import com.tracko.app.ui.theme.PriorityUrgent
import com.tracko.app.ui.theme.StatusApproved
import com.tracko.app.ui.theme.StatusCancelled
import com.tracko.app.ui.theme.StatusDraft
import com.tracko.app.ui.theme.StatusPending
import com.tracko.app.ui.theme.StatusRejected
import com.tracko.app.ui.theme.StatusSubmitted

@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        color = Color.White,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun AttendanceStatusBadge(status: String, modifier: Modifier = Modifier) {
    val (color, label) = when (status.lowercase()) {
        "present" -> AttendancePresent to "Present"
        "absent" -> AttendanceAbsent to "Absent"
        "late" -> AttendanceLate to "Late"
        "half_day", "half-day" -> AttendanceLate to "Half Day"
        "check_in", "check-in" -> AttendancePresent to "Checked In"
        "check_out", "check-out" -> AttendanceLate to "Checked Out"
        else -> StatusPending to status
    }
    StatusBadge(text = label, color = color, modifier = modifier)
}

@Composable
fun ReportStatusBadge(status: String, modifier: Modifier = Modifier) {
    val (color, label) = when (status.lowercase()) {
        "draft" -> StatusDraft to "Draft"
        "submitted" -> StatusSubmitted to "Submitted"
        "approved" -> StatusApproved to "Approved"
        "rejected" -> StatusRejected to "Rejected"
        "reviewed" -> StatusSubmitted to "Reviewed"
        else -> StatusPending to status
    }
    StatusBadge(text = label, color = color, modifier = modifier)
}

@Composable
fun LeaveStatusBadge(status: String, modifier: Modifier = Modifier) {
    val (color, label) = when (status.lowercase()) {
        "pending" -> StatusPending to "Pending"
        "approved" -> StatusApproved to "Approved"
        "rejected" -> StatusRejected to "Rejected"
        "cancelled" -> StatusCancelled to "Cancelled"
        else -> StatusPending to status
    }
    StatusBadge(text = label, color = color, modifier = modifier)
}

@Composable
fun VisitStatusBadge(status: String, modifier: Modifier = Modifier) {
    val (color, label) = when (status.lowercase()) {
        "planned" -> StatusSubmitted to "Planned"
        "in_progress", "in-progress" -> StatusPending to "In Progress"
        "completed" -> StatusApproved to "Completed"
        "missed" -> StatusRejected to "Missed"
        "cancelled" -> StatusCancelled to "Cancelled"
        else -> StatusPending to status
    }
    StatusBadge(text = label, color = color, modifier = modifier)
}

@Composable
fun PriorityBadge(priority: String, modifier: Modifier = Modifier) {
    val (color, label) = when (priority.lowercase()) {
        "low" -> PriorityLow to "Low"
        "medium", "normal" -> PriorityMedium to "Medium"
        "high" -> PriorityHigh to "High"
        "urgent", "critical" -> PriorityUrgent to "Urgent"
        else -> PriorityMedium to priority
    }
    StatusBadge(text = label, color = color, modifier = modifier)
}

@Composable
fun EnquiryStatusBadge(status: String, modifier: Modifier = Modifier) {
    val (color, label) = when (status.lowercase()) {
        "new" -> StatusSubmitted to "New"
        "assigned" -> StatusPending to "Assigned"
        "in_progress", "in-progress" -> StatusPending to "In Progress"
        "quoted" -> StatusSubmitted to "Quoted"
        "won" -> StatusApproved to "Won"
        "lost" -> StatusRejected to "Lost"
        "closed" -> StatusApproved to "Closed"
        else -> StatusPending to status
    }
    StatusBadge(text = label, color = color, modifier = modifier)
}
