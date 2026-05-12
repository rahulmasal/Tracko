package com.tracko.backend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String action;
    private String entityType;
    private Long entityId;
    private String details;
    private String ipAddress;
    private String requestPath;
    private String httpMethod;
    private Integer responseStatus;
    private Long executionTimeMs;
    private LocalDateTime createdAt;
}
