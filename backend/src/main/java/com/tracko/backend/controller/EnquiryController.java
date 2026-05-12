package com.tracko.backend.controller;

import com.tracko.backend.dto.*;
import com.tracko.backend.model.Enquiry;
import com.tracko.backend.model.EnquiryFollowup;
import com.tracko.backend.security.CustomUserDetails;
import com.tracko.backend.service.EnquiryService;
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
@RequestMapping("/api/v1/enquiries")
@RequiredArgsConstructor
public class EnquiryController {

    private final EnquiryService enquiryService;

    @PostMapping
    public ResponseEntity<ApiResponse<Enquiry>> createEnquiry(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody EnquiryRequest request) {
        Enquiry enquiry = enquiryService.createEnquiry(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Enquiry created", enquiry));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Enquiry>> updateEnquiry(
            @PathVariable Long id,
            @Valid @RequestBody EnquiryRequest request) {
        Enquiry enquiry = enquiryService.updateEnquiry(id, request);
        return ResponseEntity.ok(ApiResponse.success("Enquiry updated", enquiry));
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Enquiry>> assignEnquiry(
            @PathVariable Long id,
            @RequestParam Long assignedToId) {
        Enquiry enquiry = enquiryService.assignEnquiry(id, assignedToId);
        return ResponseEntity.ok(ApiResponse.success("Enquiry assigned", enquiry));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Enquiry>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String closureNotes) {
        Enquiry enquiry = enquiryService.updateStatus(id, status, closureNotes);
        return ResponseEntity.ok(ApiResponse.success("Status updated", enquiry));
    }

    @PostMapping("/{id}/followups")
    public ResponseEntity<ApiResponse<EnquiryFollowup>> addFollowup(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody EnquiryFollowupRequest request) {
        EnquiryFollowup followup = enquiryService.addFollowup(id, userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Followup added", followup));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Enquiry>> getEnquiry(@PathVariable Long id) {
        Enquiry enquiry = enquiryService.getEnquiry(id);
        return ResponseEntity.ok(ApiResponse.success(enquiry));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Enquiry>>> getEnquiryList(
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        Page<Enquiry> enquiries = enquiryService.getEnquiryList(assignedToId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(enquiries));
    }

    @GetMapping("/funnel")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFunnelData() {
        return ResponseEntity.ok(ApiResponse.success(enquiryService.getFunnelData()));
    }

    @GetMapping("/{id}/followups")
    public ResponseEntity<ApiResponse<List<EnquiryFollowup>>> getFollowups(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(enquiryService.getFollowups(id)));
    }
}
