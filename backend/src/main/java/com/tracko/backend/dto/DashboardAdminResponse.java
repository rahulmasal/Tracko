package com.tracko.backend.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardAdminResponse {
    private OrgSummary organization;
    private List<BranchSummary> branches;
    private List<SystemAlert> systemAlerts;
    private Map<String, Object> systemHealth;
    private List<RecentActivity> recentActivities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrgSummary {
        private long totalUsers;
        private long activeUsers;
        private long totalBranches;
        private long totalCustomers;
        private long todayVisits;
        private long todayPresent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BranchSummary {
        private Long id;
        private String name;
        private long engineers;
        private double attendanceRate;
        private double avgScore;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SystemAlert {
        private String type;
        private String message;
        private String severity;
        private boolean acknowledged;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentActivity {
        private String user;
        private String action;
        private String target;
        private String time;
    }
}
