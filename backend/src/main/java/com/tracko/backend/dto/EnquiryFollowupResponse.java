package com.tracko.backend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnquiryFollowupResponse {
    private Long id;
    private Long enquiryId;
    private String notes;
    private String type;
    private LocalDateTime followupDate;
    private LocalDateTime nextFollowupDate;
    private String createdByName;
    private LocalDateTime createdAt;
}
