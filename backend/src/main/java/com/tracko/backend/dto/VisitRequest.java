package com.tracko.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitRequest {
    private Long customerId;

    @NotNull(message = "Planned date is required")
    private LocalDate plannedDate;

    private LocalTime plannedStartTime;
    private LocalTime plannedEndTime;

    @NotNull(message = "Status is required")
    private String status;

    private String type;
    private String visitPurpose;
    private Boolean isRevisit;
    private Long originalVisitId;
    private Boolean isAdhoc;
}
