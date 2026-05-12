package com.tracko.backend.controller;

import com.tracko.backend.dto.*;
import com.tracko.backend.model.Attendance;
import com.tracko.backend.model.AttendanceCorrection;
import com.tracko.backend.security.CustomUserDetails;
import com.tracko.backend.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<Attendance>> checkIn(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CheckInRequest request) {
        Attendance attendance = attendanceService.checkIn(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Check-in successful", attendance));
    }

    @PostMapping("/check-out/{attendanceId}")
    public ResponseEntity<ApiResponse<Attendance>> checkOut(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long attendanceId,
            @Valid @RequestBody CheckOutRequest request) {
        Attendance attendance = attendanceService.checkOut(userDetails.getUserId(), attendanceId, request);
        return ResponseEntity.ok(ApiResponse.success("Check-out successful", attendance));
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<Attendance>> getTodayAttendance(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Attendance attendance = attendanceService.getTodayAttendance(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(attendance));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<Attendance>>> getAttendanceHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        Page<Attendance> history = attendanceService.getAttendanceHistory(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Attendance>>> getTeamAttendance(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) LocalDate date) {
        List<Attendance> teamAttendance = attendanceService.getTeamAttendance(
            userDetails.getUserId(), date != null ? date : LocalDate.now());
        return ResponseEntity.ok(ApiResponse.success(teamAttendance));
    }

    @GetMapping("/summary/daily")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getDailySummary(
            @RequestParam(required = false) LocalDate date) {
        Map<String, Long> summary = attendanceService.getDailySummary(
            date != null ? date : LocalDate.now());
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/summary/monthly")
    public ResponseEntity<ApiResponse<List<Attendance>>> getMonthlySummary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam int year, @RequestParam int month) {
        List<Attendance> summary = attendanceService.getMonthlySummary(userDetails.getUserId(), year, month);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @PostMapping("/corrections")
    public ResponseEntity<ApiResponse<AttendanceCorrection>> requestCorrection(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long attendanceId,
            @RequestParam String correctionType,
            @RequestParam String reason,
            @RequestParam String requestedValue) {
        AttendanceCorrection correction = attendanceService.requestCorrection(
            userDetails.getUserId(), attendanceId, correctionType, reason, requestedValue);
        return ResponseEntity.ok(ApiResponse.success("Correction requested", correction));
    }

    @GetMapping("/corrections/pending")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<AttendanceCorrection>>> getPendingCorrections() {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getPendingCorrections()));
    }

    @PostMapping("/corrections/{correctionId}/review")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<AttendanceCorrection>> reviewCorrection(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long correctionId,
            @RequestParam String action,
            @RequestParam(required = false) String comments) {
        AttendanceCorrection correction = attendanceService.reviewCorrection(
            correctionId, action, comments, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Correction reviewed", correction));
    }
}
