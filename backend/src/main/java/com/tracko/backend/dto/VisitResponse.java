package com.tracko.backend.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String employeeCode;
    private Long customerId;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private LocalDate plannedDate;
    private LocalTime plannedStartTime;
    private LocalTime plannedEndTime;
    private String status;
    private String type;
    private String visitPurpose;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Double checkInLat;
    private Double checkInLng;
    private Double checkOutLat;
    private Double checkOutLng;
    private Integer timeOnSiteMinutes;
    private String noVisitReason;
    private Boolean isRevisit;
    private Boolean isAdhoc;
    private Integer feedbackRating;
    private String feedbackNotes;
    private Boolean geofenceVerified;
}
