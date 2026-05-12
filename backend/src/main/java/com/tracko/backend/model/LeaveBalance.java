package com.tracko.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "leave_type", nullable = false, length = 50)
    private String leaveType;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "total_allocated")
    private Double totalAllocated;

    @Column(name = "used")
    private Double used = 0.0;

    @Column(name = "pending")
    private Double pending = 0.0;

    @Column(name = "remaining")
    private Double remaining;

    @Column(name = "carried_forward")
    private Double carriedForward = 0.0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        remaining = totalAllocated - used - pending + carriedForward;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        remaining = totalAllocated - used - pending + carriedForward;
    }
}
