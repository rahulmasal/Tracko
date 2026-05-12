package com.tracko.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "visits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerMaster customer;

    @Column(name = "planned_date", nullable = false)
    private LocalDate plannedDate;

    @Column(name = "planned_start_time")
    private LocalTime plannedStartTime;

    @Column(name = "planned_end_time")
    private LocalTime plannedEndTime;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(length = 50)
    private String type;

    @Column(name = "visit_purpose", length = 500)
    private String visitPurpose;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "check_in_lat")
    private Double checkInLat;

    @Column(name = "check_in_lng")
    private Double checkInLng;

    @Column(name = "check_out_lat")
    private Double checkOutLat;

    @Column(name = "check_out_lng")
    private Double checkOutLng;

    @Column(name = "check_in_photo_url", length = 500)
    private String checkInPhotoUrl;

    @Column(name = "check_out_photo_url", length = 500)
    private String checkOutPhotoUrl;

    @Column(name = "time_on_site_minutes")
    private Integer timeOnSiteMinutes;

    @Column(name = "no_visit_reason", length = 500)
    private String noVisitReason;

    @Column(name = "is_revisit")
    private Boolean isRevisit = false;

    @Column(name = "original_visit_id")
    private Long originalVisitId;

    @Column(name = "is_adhoc")
    private Boolean isAdhoc = false;

    @Column(name = "customer_signature_url", length = 500)
    private String customerSignatureUrl;

    @Column(name = "feedback_rating")
    private Integer feedbackRating;

    @Column(name = "feedback_notes", length = 1000)
    private String feedbackNotes;

    @Column(name = "geofence_verified")
    private Boolean geofenceVerified = false;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

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
