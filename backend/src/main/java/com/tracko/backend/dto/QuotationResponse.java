package com.tracko.backend.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationResponse {
    private Long id;
    private String quotationNumber;
    private Integer quoteVersion;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    private String createdByName;
    private LocalDateTime quoteDate;
    private LocalDateTime validUntil;
    private String status;
    private Double subTotal;
    private Double taxPercentage;
    private Double taxAmount;
    private Double discountPercentage;
    private Double discountAmount;
    private Double grandTotal;
    private String terms;
    private String notes;
    private String approvalStatus;
    private String approvedByName;
    private String rejectionReason;
    private String customerResponse;
    private LocalDateTime sentToCustomerAt;
    private String sentChannel;
    private LocalDateTime slaDeadline;
    private Boolean slaBreached;
    private List<QuotationItemResponse> items;
    private List<QuotationApprovalResponse> approvals;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuotationItemResponse {
        private Long id;
        private String itemName;
        private String description;
        private Integer quantity;
        private Double unitPrice;
        private Double total;
        private String hsnCode;
        private Double taxRate;
        private Double taxAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuotationApprovalResponse {
        private Long id;
        private String approverName;
        private String action;
        private String comments;
        private LocalDateTime actionAt;
    }
}
