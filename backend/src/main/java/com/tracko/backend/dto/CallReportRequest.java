package com.tracko.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallReportRequest {
    private Long customerId;
    private Long visitId;

    @NotNull(message = "Report date is required")
    private LocalDateTime reportDate;

    private String description;
    private String workDone;
    private String partsUsed;
    private String recommendations;
    private String status;
    private String customerName;
    private String customerPhone;
    private LocalDateTime jobStartTime;
    private LocalDateTime jobEndTime;
    private List<String> photoIds;
}
