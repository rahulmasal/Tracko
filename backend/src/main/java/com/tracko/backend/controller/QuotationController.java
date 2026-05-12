package com.tracko.backend.controller;

import com.tracko.backend.dto.*;
import com.tracko.backend.model.Quotation;
import com.tracko.backend.report.PdfGeneratorService;
import com.tracko.backend.security.CustomUserDetails;
import com.tracko.backend.service.QuotationService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/quotations")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;
    private final PdfGeneratorService pdfGeneratorService;

    @PostMapping
    public ResponseEntity<ApiResponse<Quotation>> createQuotation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody QuotationRequest request) {
        Quotation quotation = quotationService.createQuotation(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Quotation created", quotation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Quotation>> getQuotation(@PathVariable Long id) {
        Quotation quotation = quotationService.getQuotation(id);
        return ResponseEntity.ok(ApiResponse.success(quotation));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<Quotation>> submitForApproval(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        Quotation quotation = quotationService.submitForApproval(id, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Submitted for approval", quotation));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Quotation>> approve(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestParam(required = false) String comments) {
        Quotation quotation = quotationService.approveQuotation(id, userDetails.getUserId(), comments);
        return ResponseEntity.ok(ApiResponse.success("Quotation approved", quotation));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Quotation>> reject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestParam String comments) {
        Quotation quotation = quotationService.rejectQuotation(id, userDetails.getUserId(), comments);
        return ResponseEntity.ok(ApiResponse.success("Quotation rejected", quotation));
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<ApiResponse<Quotation>> sendToCustomer(
            @PathVariable Long id,
            @Valid @RequestBody QuotationSendRequest request) {
        Quotation quotation = quotationService.sendToCustomer(
            id, request.getChannel(), request.getEmail(), request.getPhone());
        return ResponseEntity.ok(ApiResponse.success("Quotation sent", quotation));
    }

    @PostMapping("/{id}/customer-response")
    public ResponseEntity<ApiResponse<Quotation>> recordCustomerResponse(
            @PathVariable Long id,
            @RequestParam String response,
            @RequestParam(required = false) String notes) {
        Quotation quotation = quotationService.recordCustomerResponse(id, response, notes);
        return ResponseEntity.ok(ApiResponse.success("Response recorded", quotation));
    }

    @PostMapping("/{id}/revise")
    public ResponseEntity<ApiResponse<Quotation>> revise(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody QuotationRequest request) {
        Quotation quotation = quotationService.reviseQuotation(id, userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Quotation revised", quotation));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        Quotation quotation = quotationService.getQuotation(id);
        byte[] pdf = pdfGeneratorService.generateQuotationPdf(quotation);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "quotation-" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Quotation>>> listQuotations(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        Page<Quotation> quotations = quotationService.listQuotations(userId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(quotations));
    }

    @GetMapping("/pending-approvals")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Quotation>>> getPendingApprovals() {
        return ResponseEntity.ok(ApiResponse.success(quotationService.getPendingApprovals()));
    }

    @GetMapping("/sla-breaches")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Quotation>>> getSlaBreaches() {
        return ResponseEntity.ok(ApiResponse.success(quotationService.getSlaBreaches()));
    }
}
