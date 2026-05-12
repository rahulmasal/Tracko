package com.tracko.backend.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String employeeCode;
    private LocalDate attendanceDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Double checkInLat;
    private Double checkInLng;
    private Double checkOutLat;
    private Double checkOutLng;
    private String checkInLocation;
    private String checkOutLocation;
    private String status;
    private String statusReason;
    private Boolean isLate;
    private Integer lateMinutes;
    private Boolean isOvertime;
    private Integer overtimeMinutes;
    private Double totalWorkingHours;
    private Boolean geofenceVerified;
}
