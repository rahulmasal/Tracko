package com.tracko.backend.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallReportResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String employeeCode;
    private Long customerId;
    private String customerName;
    private Long visitId;
    private LocalDateTime reportDate;
    private String description;
    private String workDone;
    private String partsUsed;
    private String recommendations;
    private String status;
    private String submissionStatus;
    private String reviewStatus;
    private String reviewedBy;
    private String reviewComments;
    private String customerSignatureUrl;
    private String customerPhone;
    private LocalDateTime jobStartTime;
    private LocalDateTime jobEndTime;
    private Double totalHours;
    private List<String> photoUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
