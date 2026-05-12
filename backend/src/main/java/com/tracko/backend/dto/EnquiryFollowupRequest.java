package com.tracko.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnquiryFollowupRequest {
    @NotBlank(message = "Notes are required")
    private String notes;

    private String type;
    private LocalDateTime nextFollowupDate;
}
