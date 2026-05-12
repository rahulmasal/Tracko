package com.tracko.backend.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardManagerResponse {
    private TeamSummary team;
    private List<EngineerStatus> engineers;
    private List<Alert> alerts;
    private AttendanceSummaryResponse attendanceSummary;
    private Map<String, Double> performanceMetrics;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TeamSummary {
        private int totalEngineers;
        private int activeNow;
        private int onLeave;
        private int onSite;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EngineerStatus {
        private Long id;
        private String name;
        private String status;
        private String lastLocation;
        private String lastUpdate;
        private Double todayScore;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Alert {
        private String type;
        private String message;
        private String severity;
        private String time;
    }
}
