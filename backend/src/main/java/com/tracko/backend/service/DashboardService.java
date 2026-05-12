package com.tracko.backend.service;

import com.tracko.backend.dto.*;
import com.tracko.backend.model.*;
import com.tracko.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final VisitRepository visitRepository;
    private final CallReportRepository callReportRepository;
    private final NotificationRepository notificationRepository;
    private final ScoreCardRepository scoreCardRepository;

    public DashboardEmployeeResponse getEmployeeDashboard(Long userId) {
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        Attendance todayAttendance = attendanceRepository
            .findByUserIdAndAttendanceDate(userId, today).orElse(null);

        DashboardEmployeeResponse.AttendanceSummary attendance =
            DashboardEmployeeResponse.AttendanceSummary.builder()
                .todayStatus(todayAttendance != null ? todayAttendance.getStatus() : "NOT_MARKED")
                .checkInTime(todayAttendance != null && todayAttendance.getCheckInTime() != null
                    ? todayAttendance.getCheckInTime().toString() : null)
                .checkOutTime(todayAttendance != null && todayAttendance.getCheckOutTime() != null
                    ? todayAttendance.getCheckOutTime().toString() : null)
                .totalHours(todayAttendance != null ? todayAttendance.getTotalWorkingHours() : 0)
                .checkedIn(todayAttendance != null && todayAttendance.getCheckInTime() != null)
                .checkedOut(todayAttendance != null && todayAttendance.getCheckOutTime() != null)
                .build();

        List<Visit> todayVisits = visitRepository.findByUserIdAndPlannedDate(userId, today);
        List<DashboardEmployeeResponse.VisitSummary> visitSummaries = todayVisits.stream()
            .map(v -> DashboardEmployeeResponse.VisitSummary.builder()
                .id(v.getId())
                .customerName(v.getCustomer() != null ? v.getCustomer().getName() : "N/A")
                .status(v.getStatus())
                .plannedTime(v.getPlannedStartTime() != null ? v.getPlannedStartTime().toString() : null)
                .type(v.getType())
                .build())
            .collect(Collectors.toList());

        List<CallReport> pendingReports = callReportRepository.findUnsubmittedByUserId(userId);
        long unreadNotifications = notificationRepository.countByUserIdAndIsRead(userId, false);
        ScoreCard score = scoreCardRepository
            .findByUserIdAndScoreMonthAndScoreYear(userId, currentMonth, currentYear).orElse(null);

        List<DashboardEmployeeResponse.UpcomingTask> tasks = new ArrayList<>();
        if (todayAttendance == null || todayAttendance.getCheckInTime() == null) {
            tasks.add(DashboardEmployeeResponse.UpcomingTask.builder()
                .type("ATTENDANCE")
                .description("Check in for the day")
                .dueTime("Now")
                .build());
        }

        return DashboardEmployeeResponse.builder()
            .attendance(attendance)
            .todayVisits(visitSummaries)
            .pendingReports(pendingReports.size())
            .unreadNotifications((int) unreadNotifications)
            .score(DashboardEmployeeResponse.ScoreSummary.builder()
                .totalScore(score != null ? score.getTotalScore() : 0)
                .rating(score != null ? score.getRating() : "N/A")
                .trend("STABLE")
                .build())
            .upcomingTasks(tasks)
            .build();
    }

    public DashboardManagerResponse getManagerDashboard(Long managerId) {
        List<User> teamMembers = userRepository.findByManagerId(managerId);
        LocalDate today = LocalDate.now();

        DashboardManagerResponse.TeamSummary team = DashboardManagerResponse.TeamSummary.builder()
            .totalEngineers(teamMembers.size())
            .activeNow(0)
            .onLeave(0)
            .onSite(0)
            .build();

        List<DashboardManagerResponse.EngineerStatus> engineerStatuses = new ArrayList<>();
        for (User member : teamMembers) {
            Attendance att = attendanceRepository
                .findByUserIdAndAttendanceDate(member.getId(), today).orElse(null);
            String status = att != null ? att.getStatus() : "ABSENT";

            engineerStatuses.add(DashboardManagerResponse.EngineerStatus.builder()
                .id(member.getId())
                .name(member.getFullName())
                .status(status)
                .lastLocation("N/A")
                .lastUpdate("N/A")
                .todayScore(0.0)
                .build());
        }

        List<Object[]> summaryData = attendanceRepository.getStatusSummaryByDate(today);
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] row : summaryData) {
            statusMap.put((String) row[0], (Long) row[1]);
        }

        return DashboardManagerResponse.builder()
            .team(team)
            .engineers(engineerStatuses)
            .alerts(new ArrayList<>())
            .attendanceSummary(AttendanceSummaryResponse.builder()
                .statusBreakdown(statusMap)
                .build())
            .performanceMetrics(new HashMap<>())
            .build();
    }

    public DashboardAdminResponse getAdminDashboard() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countActiveUsers();

        return DashboardAdminResponse.builder()
            .organization(DashboardAdminResponse.OrgSummary.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .build())
            .branches(new ArrayList<>())
            .systemAlerts(new ArrayList<>())
            .systemHealth(new HashMap<>())
            .recentActivities(new ArrayList<>())
            .build();
    }
}
