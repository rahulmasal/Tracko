package com.tracko.backend.controller;

import com.tracko.backend.dto.*;
import com.tracko.backend.model.CallReport;
import com.tracko.backend.report.PdfGeneratorService;
import com.tracko.backend.security.CustomUserDetails;
import com.tracko.backend.service.CallReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/call-reports")
@RequiredArgsConstructor
public class CallReportController {

    private final CallReportService callReportService;
    private final PdfGeneratorService pdfGeneratorService;

    @PostMapping
    public ResponseEntity<ApiResponse<CallReport>> createReport(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CallReportRequest request) {
        CallReport report = callReportService.createReport(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Report created", report));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CallReport>> updateReport(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody CallReportRequest request) {
        CallReport report = callReportService.updateReport(id, userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Report updated", report));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<CallReport>> submitReport(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        CallReport report = callReportService.submitReport(id, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Report submitted", report));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CallReport>> getReport(@PathVariable Long id) {
        CallReport report = callReportService.getReport(id);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CallReport>> reviewReport(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ReportReviewRequest request) {
        CallReport report = callReportService.reviewReport(
            id, request.getAction(), request.getComments(), userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Report reviewed", report));
    }

    @PostMapping("/{id}/photos")
    public ResponseEntity<ApiResponse<Void>> uploadPhotos(
            @PathVariable Long id,
            @RequestParam List<MultipartFile> photos) {
        return ResponseEntity.ok(ApiResponse.success("Photos uploaded"));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        CallReport report = callReportService.getReport(id);
        byte[] pdf = pdfGeneratorService.generateCallReportPdf(report);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "call-report-" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<CallReport>>> getReportHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        Page<CallReport> history = callReportService.getReportHistory(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CallReport>>> getPendingReports() {
        return ResponseEntity.ok(ApiResponse.success(callReportService.getPendingReports()));
    }

    @GetMapping("/unsubmitted")
    public ResponseEntity<ApiResponse<List<CallReport>>> getUnsubmittedReports(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(callReportService.getUnsubmittedReports(userDetails.getUserId())));
    }
}
