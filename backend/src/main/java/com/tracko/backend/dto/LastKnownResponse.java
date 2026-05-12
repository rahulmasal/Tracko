package com.tracko.backend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LastKnownResponse {
    private Long userId;
    private String userName;
    private Double lat;
    private Double lng;
    private LocalDateTime recordedAt;
    private Float speed;
    private Float accuracy;
    private Integer batteryLevel;
    private Integer minutesSinceUpdate;
}
