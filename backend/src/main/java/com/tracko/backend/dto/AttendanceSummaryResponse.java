package com.tracko.backend.dto;

import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSummaryResponse {
    private long totalPresent;
    private long totalLate;
    private long totalAbsent;
    private long totalHalfDay;
    private long totalOnLeave;
    private double attendancePercentage;
    private Map<String, Long> statusBreakdown;
}
