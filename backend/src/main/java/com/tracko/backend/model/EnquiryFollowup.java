package com.tracko.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enquiry_followups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnquiryFollowup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enquiry_id", nullable = false)
    private Enquiry enquiry;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(length = 50)
    private String type;

    @Column(name = "followup_date")
    private LocalDateTime followupDate;

    @Column(name = "next_followup_date")
    private LocalDateTime nextFollowupDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
