package com.tracko.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracko.backend.model.AuditLog;
import com.tracko.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logEvent(Long userId, String action, String entityType, Long entityId,
                         Object beforeSnapshot, Object afterSnapshot, String ipAddress,
                         String requestPath, String httpMethod, Integer responseStatus,
                         Long executionTimeMs, String details) {
        try {
            AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .beforeSnapshot(beforeSnapshot != null ? objectMapper.writeValueAsString(beforeSnapshot) : null)
                .afterSnapshot(afterSnapshot != null ? objectMapper.writeValueAsString(afterSnapshot) : null)
                .ipAddress(ipAddress)
                .requestPath(requestPath)
                .httpMethod(httpMethod)
                .responseStatus(responseStatus)
                .executionTimeMs(executionTimeMs)
                .details(details)
                .build();
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    public Page<AuditLog> getAuditLogs(Long userId, String action, String entityType,
                                       LocalDateTime from, LocalDateTime to,
                                       Pageable pageable) {
        if (userId != null) {
            return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }
        if (action != null) {
            return auditLogRepository.findByActionOrderByCreatedAtDesc(action, pageable);
        }
        return auditLogRepository.findAll(pageable);
    }

    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    public Page<AuditLog> getByEntity(String entityType, Long entityId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            entityType, entityId, pageable);
    }

    public long getAuditSummary() {
        return auditLogRepository.count();
    }
}
