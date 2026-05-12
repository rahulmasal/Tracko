package com.tracko.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnquiryRequest {
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    @NotBlank(message = "Enquiry type is required")
    private String enquiryType;

    @NotBlank(message = "Description is required")
    private String description;

    private String priority;
    private Long assignedTo;
    private LocalDateTime expectedClosureDate;
    private String source;
}
