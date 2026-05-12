package com.tracko.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_logs", indexes = {
    @Index(name = "idx_tracking_user_recorded", columnList = "user_id, recorded_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    private Double altitude;

    private Float accuracy;

    private Float speed;

    @Column(name = "bearing")
    private Float bearing;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "battery_level")
    private Integer batteryLevel;

    @Column(length = 20)
    private String provider;

    @Column(name = "is_mocked")
    private Boolean isMocked = false;

    @Column(name = "activity_type", length = 50)
    private String activityType;

    @Column(name = "activity_confidence")
    private Integer activityConfidence;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (receivedAt == null) receivedAt = LocalDateTime.now();
    }
}
