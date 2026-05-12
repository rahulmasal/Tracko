package com.tracko.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EmployeeDashboardResponse(
    @SerializedName("attendance") val attendance: AttendanceResponse? = null,
    @SerializedName("todayVisitCount") val todayVisitCount: Int = 0,
    @SerializedName("completedVisitCount") val completedVisitCount: Int = 0,
    @SerializedName("pendingReportCount") val pendingReportCount: Int = 0,
    @SerializedName("leaveBalance") val leaveBalance: LeaveBalanceResponse? = null,
    @SerializedName("currentMonthScore") val currentMonthScore: ScoreSummary? = null,
    @SerializedName("pendingLeaveCount") val pendingLeaveCount: Int = 0,
    @SerializedName("unreadNotificationCount") val unreadNotificationCount: Int = 0,
    @SerializedName("recentActivity") val recentActivity: List<ActivityDto>? = null
)

data class ManagerDashboardResponse(
    @SerializedName("teamSize") val teamSize: Int = 0,
    @SerializedName("todayPresent") val todayPresent: Int = 0,
    @SerializedName("todayAbsent") val todayAbsent: Int = 0,
    @SerializedName("todayOnLeave") val todayOnLeave: Int = 0,
    @SerializedName("pendingApprovals") val pendingApprovals: Int = 0,
    @SerializedName("pendingReports") val pendingReports: Int = 0,
    @SerializedName("teamVisitsToday") val teamVisitsToday: Int = 0,
    @SerializedName("teamCompliance") val teamCompliance: Double = 0.0,
    @SerializedName("teamMembers") val teamMembers: List<TeamMemberSummary>? = null
)

data class AdminDashboardResponse(
    @SerializedName("totalEmployees") val totalEmployees: Int = 0,
    @SerializedName("activeToday") val activeToday: Int = 0,
    @SerializedName("onLeaveToday") val onLeaveToday: Int = 0,
    @SerializedName("totalVisitsToday") val totalVisitsToday: Int = 0,
    @SerializedName("completedVisitsToday") val completedVisitsToday: Int = 0,
    @SerializedName("pendingReports") val pendingReports: Int = 0,
    @SerializedName("openEnquiries") val openEnquiries: Int = 0,
    @SerializedName("overallCompliance") val overallCompliance: Double = 0.0,
    @SerializedName("departmentWise") val departmentWise: List<DepartmentSummary>? = null
)

data class ScoreSummary(
    @SerializedName("overall") val overall: Int = 0,
    @SerializedName("attendance") val attendance: Int = 0,
    @SerializedName("visits") val visits: Int = 0,
    @SerializedName("reports") val reports: Int = 0,
    @SerializedName("punctuality") val punctuality: Int = 0,
    @SerializedName("previousMonth") val previousMonth: Int? = null,
    @SerializedName("teamRank") val teamRank: Int? = null,
    @SerializedName("trend") val trend: String? = null
)

data class ActivityDto(
    @SerializedName("type") val type: String,
    @SerializedName("description") val description: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("referenceId") val referenceId: String? = null,
    @SerializedName("referenceType") val referenceType: String? = null
)

data class TeamMemberSummary(
    @SerializedName("userId") val userId: String,
    @SerializedName("employeeName") val employeeName: String,
    @SerializedName("status") val status: String,
    @SerializedName("todayVisits") val todayVisits: Int = 0,
    @SerializedName("completedVisits") val completedVisits: Int = 0,
    @SerializedName("score") val score: Int? = null
)

data class DepartmentSummary(
    @SerializedName("department") val department: String,
    @SerializedName("employeeCount") val employeeCount: Int,
    @SerializedName("presentToday") val presentToday: Int,
    @SerializedName("compliance") val compliance: Double
)
