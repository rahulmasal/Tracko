package com.tracko.app.ui.navigation

sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object Login : Routes("login")
    data object DeviceWarning : Routes("device_warning")
    data object EmployeeDashboard : Routes("employee_dashboard")
    data object ManagerDashboard : Routes("manager_dashboard")
    data object AdminDashboard : Routes("admin_dashboard")
    data object CheckIn : Routes("check_in")
    data object CheckOut : Routes("check_out")
    data object VisitList : Routes("visit_list")
    data object VisitDetail : Routes("visit_detail/{visitId}") {
        fun createRoute(visitId: Long) = "visit_detail/$visitId"
    }
    data object NewReport : Routes("new_report/{visitId}") {
        fun createRoute(visitId: Long? = null) = "new_report/${visitId ?: -1}"
    }
    data object EditReport : Routes("edit_report/{reportId}") {
        fun createRoute(reportId: Long) = "edit_report/$reportId"
    }
    data object ReportHistory : Routes("report_history")
    data object ReportDetail : Routes("report_detail/{reportId}") {
        fun createRoute(reportId: Long) = "report_detail/$reportId"
    }
    data object EnquiryCreate : Routes("enquiry_create")
    data object EnquiryList : Routes("enquiry_list")
    data object EnquiryDetail : Routes("enquiry_detail/{enquiryId}") {
        fun createRoute(enquiryId: Long) = "enquiry_detail/$enquiryId"
    }
    data object LeaveApply : Routes("leave_apply")
    data object LeaveHistory : Routes("leave_history")
    data object Profile : Routes("profile")
    data object Notifications : Routes("notifications")
    data object ScoreCard : Routes("score_card")
}
