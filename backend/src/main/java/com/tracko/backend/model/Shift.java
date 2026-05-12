package com.tracko.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "shifts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "grace_minutes")
    private Integer graceMinutes = 15;

    @Column(name = "late_threshold_minutes")
    private Integer lateThresholdMinutes = 30;

    @Column(name = "half_day_threshold_minutes")
    private Integer halfDayThresholdMinutes = 120;

    @Column(name = "overtime_threshold_minutes")
    private Integer overtimeThresholdMinutes = 60;

    @Column(name = "is_night_shift")
    private Boolean isNightShift = false;

    @Column(name = "applicable_days", length = 100)
    private String applicableDays;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "branch_id")
    private Long branchId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
