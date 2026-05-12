package com.tracko.backend.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreTrendResponse {
    private Long userId;
    private String userName;
    private List<MonthlyScore> scores;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyScore {
        private Integer month;
        private Integer year;
        private Double totalScore;
        private String rating;
    }
}
