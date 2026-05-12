package com.tracko.backend.controller;

import com.tracko.backend.dto.*;
import com.tracko.backend.model.LeaveBalance;
import com.tracko.backend.model.LeaveRequest;
import com.tracko.backend.security.CustomUserDetails;
import com.tracko.backend.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping
    public ResponseEntity<ApiResponse<LeaveRequest>> applyLeave(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody LeaveRequestDto request) {
        LeaveRequest leave = leaveService.applyLeave(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Leave applied", leave));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveRequest>> getLeave(@PathVariable Long id) {
        LeaveRequest leave = leaveService.getLeave(id);
        return ResponseEntity.ok(ApiResponse.success(leave));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<LeaveRequest>> cancelLeave(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        LeaveRequest leave = leaveService.cancelLeave(id, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Leave cancelled", leave));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LeaveRequest>> approveLeave(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestParam(required = false) String comments) {
        LeaveRequest leave = leaveService.approveLeave(id, userDetails.getUserId(), comments);
        return ResponseEntity.ok(ApiResponse.success("Leave approved", leave));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LeaveRequest>> rejectLeave(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestParam String comments) {
        LeaveRequest leave = leaveService.rejectLeave(id, userDetails.getUserId(), comments);
        return ResponseEntity.ok(ApiResponse.success("Leave rejected", leave));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<LeaveRequest>>> getLeaveHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        Page<LeaveRequest> history = leaveService.getLeaveHistory(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<List<LeaveBalance>>> getLeaveBalance(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<LeaveBalance> balance = leaveService.getLeaveBalance(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(balance));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<LeaveRequest>>> getPendingLeaves(Pageable pageable) {
        Page<LeaveRequest> pending = leaveService.getPendingLeaves(pageable);
        return ResponseEntity.ok(ApiResponse.success(pending));
    }

    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCalendar(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam int year, @RequestParam int month) {
        Map<String, Object> calendar = leaveService.getCalendar(userDetails.getUserId(), year, month);
        return ResponseEntity.ok(ApiResponse.success(calendar));
    }

    @GetMapping("/team-conflicts")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LeaveRequest>>> getTeamConflicts(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<LeaveRequest> conflicts = leaveService.getTeamConflicts(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(conflicts));
    }
}
