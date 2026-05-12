package com.tracko.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceSecurityCheckRequest {
    @NotBlank(message = "Device ID is required")
    private String deviceId;

    private String osVersion;
    private String appVersion;
    private String deviceModel;
    private Boolean isRooted;
    private Boolean isMockLocationEnabled;
    private Boolean isDeveloperMode;
    private Boolean isScreenLockEnabled;
    private Boolean isPlayProtectEnabled;
    private Boolean isUnknownSources;
}
