package com.tracko.backend.controller;

import com.tracko.backend.dto.ApiResponse;
import com.tracko.backend.model.AuditLog;
import com.tracko.backend.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/audit")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            Pageable pageable) {
        Page<AuditLog> logs = auditService.getAuditLogs(userId, action, entityType, from, to, pageable);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getEntityAuditLogs(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            Pageable pageable) {
        Page<AuditLog> logs = auditService.getByEntity(entityType, entityId, pageable);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/security-events")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getSecurityEvents(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
            auditService.getAuditLogs(null, "SECURITY_EVENT", null, null, null, pageable)));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAuditSummary() {
        long totalLogs = auditService.getAuditSummary();
        return ResponseEntity.ok(ApiResponse.success(Map.of("totalLogs", totalLogs)));
    }
}
