package com.tracko.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationSendRequest {
    @NotNull(message = "Quotation ID is required")
    private Long quotationId;

    @NotBlank(message = "Channel is required")
    private String channel;

    private String email;
    private String phone;
}
