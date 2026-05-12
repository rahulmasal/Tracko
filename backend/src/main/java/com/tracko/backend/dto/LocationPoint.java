package com.tracko.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationPoint {
    @NotNull(message = "Latitude is required")
    private Double lat;

    @NotNull(message = "Longitude is required")
    private Double lng;

    private Double altitude;
    private Float accuracy;
    private Float speed;
    private Float bearing;

    @NotNull(message = "Recorded at timestamp is required")
    private LocalDateTime recordedAt;

    private Integer batteryLevel;
    private String provider;
    private Boolean isMocked;
    private String activityType;
    private Integer activityConfidence;
}
