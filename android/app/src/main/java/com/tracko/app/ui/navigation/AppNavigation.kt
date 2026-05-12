package com.tracko.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tracko.app.ui.attendance.CheckInScreen
import com.tracko.app.ui.attendance.CheckOutScreen
import com.tracko.app.ui.auth.DeviceWarningScreen
import com.tracko.app.ui.auth.LoginScreen
import com.tracko.app.ui.dashboard.EmployeeDashboardScreen
import com.tracko.app.ui.enquiries.EnquiryCreateScreen
import com.tracko.app.ui.enquiries.EnquiryListScreen
import com.tracko.app.ui.leaves.LeaveApplyScreen
import com.tracko.app.ui.leaves.LeaveHistoryScreen
import com.tracko.app.ui.notifications.NotificationScreen
import com.tracko.app.ui.profile.ProfileScreen
import com.tracko.app.ui.reports.CallReportFormScreen
import com.tracko.app.ui.reports.CallReportHistoryScreen
import com.tracko.app.ui.score.ScoreCardScreen
import com.tracko.app.ui.visits.VisitDetailScreen
import com.tracko.app.ui.visits.VisitListScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        composable(Routes.Splash.route) {
            // SplashScreen(navController)
        }

        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DeviceWarning.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DeviceWarning.route) {
            DeviceWarningScreen(
                onProceed = {
                    navController.navigate(Routes.EmployeeDashboard.route) {
                        popUpTo(Routes.DeviceWarning.route) { inclusive = true }
                    }
                },
                onBlocked = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.DeviceWarning.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.EmployeeDashboard.route) {
            EmployeeDashboardScreen(
                onNavigateToCheckIn = { navController.navigate(Routes.CheckIn.route) },
                onNavigateToCheckOut = { navController.navigate(Routes.CheckOut.route) },
                onNavigateToVisits = { navController.navigate(Routes.VisitList.route) },
                onNavigateToReports = { navController.navigate(Routes.ReportHistory.route) },
                onNavigateToLeaves = { navController.navigate(Routes.LeaveHistory.route) },
                onNavigateToProfile = { navController.navigate(Routes.Profile.route) },
                onNavigateToNotifications = { navController.navigate(Routes.Notifications.route) },
                onNavigateToScore = { navController.navigate(Routes.ScoreCard.route) },
                onNavigateToEnquiries = { navController.navigate(Routes.EnquiryList.route) },
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CheckIn.route) {
            CheckInScreen(
                onSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CheckOut.route) {
            CheckOutScreen(
                onSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.VisitList.route) {
            VisitListScreen(
                onVisitClick = { visitId ->
                    navController.navigate(Routes.VisitDetail.createRoute(visitId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.VisitDetail.route,
            arguments = listOf(navArgument("visitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val visitId = backStackEntry.arguments?.getLong("visitId") ?: return@composable
            VisitDetailScreen(
                visitId = visitId,
                onBack = { navController.popBackStack() },
                onCreateReport = { vid ->
                    navController.navigate(Routes.NewReport.createRoute(vid))
                },
                onNavigateToMaps = { lat, lng -> }
            )
        }

        composable(
            route = Routes.NewReport.route,
            arguments = listOf(navArgument("visitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val visitId = backStackEntry.arguments?.getLong("visitId") ?: -1L
            CallReportFormScreen(
                visitId = if (visitId == -1L) null else visitId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Routes.ReportHistory.route) {
            CallReportHistoryScreen(
                onReportClick = { reportId ->
                    navController.navigate(Routes.ReportDetail.createRoute(reportId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.EnquiryCreate.route) {
            EnquiryCreateScreen(
                onBack = { navController.popBackStack() },
                onCreated = { navController.popBackStack() }
            )
        }

        composable(Routes.EnquiryList.route) {
            EnquiryListScreen(
                onEnquiryClick = { enquiryId ->
                    // navController.navigate(Routes.EnquiryDetail.createRoute(enquiryId))
                },
                onCreateNew = { navController.navigate(Routes.EnquiryCreate.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.LeaveApply.route) {
            LeaveApplyScreen(
                onBack = { navController.popBackStack() },
                onApplied = { navController.popBackStack() }
            )
        }

        composable(Routes.LeaveHistory.route) {
            LeaveHistoryScreen(
                onApplyLeave = { navController.navigate(Routes.LeaveApply.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Notifications.route) {
            NotificationScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ScoreCard.route) {
            ScoreCardScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
