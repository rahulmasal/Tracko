package com.tracko.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportReviewRequest {
    @NotNull(message = "Report ID is required")
    private Long reportId;

    @NotBlank(message = "Action is required")
    private String action;

    private String comments;
}
