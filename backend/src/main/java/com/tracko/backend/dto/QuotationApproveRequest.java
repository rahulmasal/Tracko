package com.tracko.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationApproveRequest {
    @NotNull(message = "Quotation ID is required")
    private Long quotationId;

    @NotBlank(message = "Action is required")
    private String action;

    private String comments;
}
