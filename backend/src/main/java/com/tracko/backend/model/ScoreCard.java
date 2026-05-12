package com.tracko.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "score_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "score_month", nullable = false)
    private Integer scoreMonth;

    @Column(name = "score_year", nullable = false)
    private Integer scoreYear;

    @Column(name = "visit_completion_score")
    private Double visitCompletionScore;

    @Column(name = "on_time_attendance_score")
    private Double onTimeAttendanceScore;

    @Column(name = "report_completeness_score")
    private Double reportCompletenessScore;

    @Column(name = "tracking_compliance_score")
    private Double trackingComplianceScore;

    @Column(name = "photo_proof_score")
    private Double photoProofScore;

    @Column(name = "customer_feedback_score")
    private Double customerFeedbackScore;

    @Column(name = "repeat_visit_score")
    private Double repeatVisitScore;

    @Column(name = "enquiry_gen_score")
    private Double enquiryGenScore;

    @Column(name = "quote_followup_score")
    private Double quoteFollowupScore;

    @Column(name = "job_closure_score")
    private Double jobClosureScore;

    @Column(name = "manager_score")
    private Double managerScore;

    @Column(name = "total_score")
    private Double totalScore;

    @Column(length = 10)
    private String rating;

    @Column(name = "is_manual")
    private Boolean isManual = false;

    @Column(name = "adjusted_by")
    private Long adjustedBy;

    @Column(name = "adjustment_reason", length = 500)
    private String adjustmentReason;

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
