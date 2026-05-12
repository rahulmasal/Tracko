package com.tracko.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "missed_pings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissedPing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "expected_time")
    private LocalDateTime expectedTime;

    @Column(name = "last_known_lat")
    private Double lastKnownLat;

    @Column(name = "last_known_lng")
    private Double lastKnownLng;

    @Column(name = "last_known_time")
    private LocalDateTime lastKnownTime;

    @Column(name = "consecutive_misses")
    private Integer consecutiveMisses = 1;

    @Column(length = 20)
    private String severity;

    @Column(name = "is_resolved")
    private Boolean isResolved = false;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "escalation_level")
    private Integer escalationLevel = 0;

    @Column(name = "notified_manager")
    private Boolean notifiedManager = false;

    @Column(name = "notified_admin")
    private Boolean notifiedAdmin = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
