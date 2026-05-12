package com.tracko.backend.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnquiryResponse {
    private Long id;
    private String enquiryCode;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String enquiryType;
    private String description;
    private String status;
    private String priority;
    private Long assignedToId;
    private String assignedToName;
    private Long createdById;
    private String createdByName;
    private LocalDateTime assignedAt;
    private LocalDateTime expectedClosureDate;
    private LocalDateTime closedAt;
    private String closureNotes;
    private String source;
    private List<EnquiryFollowupResponse> followups;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
