package com.tracko.backend.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponse {
    private Long userId;
    private String userName;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private List<RoutePoint> points;
    private Double totalDistanceKm;
    private Integer totalPoints;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoutePoint {
        private Double lat;
        private Double lng;
        private LocalDateTime recordedAt;
        private Float speed;
        private Float accuracy;
    }
}
