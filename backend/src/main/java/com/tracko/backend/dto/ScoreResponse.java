package com.tracko.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String employeeCode;
    private Integer scoreMonth;
    private Integer scoreYear;
    private Double visitCompletionScore;
    private Double onTimeAttendanceScore;
    private Double reportCompletenessScore;
    private Double trackingComplianceScore;
    private Double photoProofScore;
    private Double customerFeedbackScore;
    private Double repeatVisitScore;
    private Double enquiryGenScore;
    private Double quoteFollowupScore;
    private Double jobClosureScore;
    private Double managerScore;
    private Double totalScore;
    private String rating;
}
