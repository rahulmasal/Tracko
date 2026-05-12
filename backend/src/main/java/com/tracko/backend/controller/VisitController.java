package com.tracko.backend.controller;

import com.tracko.backend.dto.*;
import com.tracko.backend.model.Visit;
import com.tracko.backend.security.CustomUserDetails;
import com.tracko.backend.service.VisitService;
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

@RestController
@RequestMapping("/api/v1/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    public ResponseEntity<ApiResponse<Visit>> createVisit(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody VisitRequest request) {
        Visit visit = visitService.createVisit(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Visit created", visit));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Visit>> updateVisit(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody VisitRequest request) {
        Visit visit = visitService.updateVisit(id, userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Visit updated", visit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Visit>> getVisit(@PathVariable Long id) {
        Visit visit = visitService.getVisitById(id);
        return ResponseEntity.ok(ApiResponse.success(visit));
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<Visit>>> getTodayVisits(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Visit> visits = visitService.getTodayVisits(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(visits));
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<ApiResponse<Visit>> checkInVisit(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody VisitCheckInRequest request) {
        Visit visit = visitService.checkInVisit(id, userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Checked in to visit", visit));
    }

    @PostMapping("/{id}/check-out")
    public ResponseEntity<ApiResponse<Visit>> checkOutVisit(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody VisitCheckInRequest request) {
        Visit visit = visitService.checkOutVisit(id, userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Checked out from visit", visit));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<Visit>>> getVisitHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        Page<Visit> history = visitService.getVisitHistory(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Visit>>> getTeamVisits(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) LocalDate date) {
        List<Visit> visits = visitService.getTeamVisits(
            userDetails.getUserId(), date != null ? date : LocalDate.now());
        return ResponseEntity.ok(ApiResponse.success(visits));
    }

    @GetMapping("/missed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Visit>>> getMissedVisits(
            @RequestParam(required = false) LocalDate date) {
        List<Visit> missedVisits = visitService.getMissedVisits(
            date != null ? date : LocalDate.now());
        return ResponseEntity.ok(ApiResponse.success(missedVisits));
    }

    @PostMapping("/adhoc")
    public ResponseEntity<ApiResponse<Visit>> createAdhocVisit(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody VisitRequest request) {
        Visit visit = visitService.createAdhocVisit(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Ad-hoc visit created", visit));
    }
}
