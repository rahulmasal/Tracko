package com.tracko.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationRequest {
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;

    @NotNull(message = "Quote date is required")
    private LocalDateTime quoteDate;

    private LocalDateTime validUntil;
    private String status;

    @NotNull(message = "At least one item is required")
    @Valid
    private List<QuotationItemRequest> items;

    private Double taxPercentage;
    private Double discountPercentage;
    private String terms;
    private String notes;
    private Long branchId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuotationItemRequest {
        @NotBlank(message = "Item name is required")
        private String itemName;

        private String description;
        private Integer quantity;
        private Double unitPrice;
        private String hsnCode;
        private Double taxRate;
    }
}
