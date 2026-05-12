package com.tracko.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceSecurityCheckResponse {
    private String deviceId;
    private int riskScore;
    private String riskLevel;
    private String policyAction;
    private String message;
    private boolean allowed;
}
