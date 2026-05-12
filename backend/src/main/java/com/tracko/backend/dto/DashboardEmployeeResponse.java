package com.tracko.backend.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardEmployeeResponse {
    private AttendanceSummary attendance;
    private List<VisitSummary> todayVisits;
    private int pendingReports;
    private int unreadNotifications;
    private ScoreSummary score;
    private List<UpcomingTask> upcomingTasks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttendanceSummary {
        private String todayStatus;
        private String checkInTime;
        private String checkOutTime;
        private Double totalHours;
        private boolean checkedIn;
        private boolean checkedOut;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VisitSummary {
        private Long id;
        private String customerName;
        private String status;
        private String plannedTime;
        private String type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScoreSummary {
        private Double totalScore;
        private String rating;
        private String trend;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpcomingTask {
        private String type;
        private String description;
        private String dueTime;
    }
}
