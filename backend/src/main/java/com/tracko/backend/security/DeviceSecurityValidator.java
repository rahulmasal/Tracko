package com.tracko.backend.security;

import com.tracko.backend.dto.DeviceSecurityCheckRequest;
import com.tracko.backend.dto.DeviceSecurityCheckResponse;
import com.tracko.backend.model.DeviceSecurityLog;
import com.tracko.backend.repository.DeviceSecurityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceSecurityValidator {

    private final DeviceSecurityLogRepository securityLogRepository;

    @Value("${app.security.device-policy.block-score-threshold:80}")
    private int blockScoreThreshold;

    @Value("${app.security.device-policy.limited-score-threshold:60}")
    private int limitedScoreThreshold;

    @Value("${app.security.device-policy.warning-score-threshold:40}")
    private int warningScoreThreshold;

    public DeviceSecurityCheckResponse validate(DeviceSecurityCheckRequest request, Long userId) {
        int riskScore = calculateRiskScore(request);
        String riskLevel = getRiskLevel(riskScore);
        String policyAction = getPolicyAction(riskScore);
        boolean allowed = !"BLOCK_ALL".equals(policyAction);

        DeviceSecurityLog logEntry = DeviceSecurityLog.builder()
            .userId(userId)
            .deviceId(request.getDeviceId())
            .osVersion(request.getOsVersion())
            .appVersion(request.getAppVersion())
            .deviceModel(request.getDeviceModel())
            .isRooted(request.getIsRooted())
            .isMockLocationEnabled(request.getIsMockLocationEnabled())
            .isDeveloperMode(request.getIsDeveloperMode())
            .isScreenLockEnabled(request.getIsScreenLockEnabled())
            .isPlayProtectEnabled(request.getIsPlayProtectEnabled())
            .isUnknownSources(request.getIsUnknownSources())
            .riskScore(riskScore)
            .riskLevel(riskLevel)
            .policyAction(policyAction)
            .checkedAt(LocalDateTime.now())
            .build();

        securityLogRepository.save(logEntry);

        if ("BLOCK_ALL".equals(policyAction)) {
            log.warn("Device BLOCKED - userId: {}, deviceId: {}, riskScore: {}",
                userId, request.getDeviceId(), riskScore);
        }

        String message = switch (policyAction) {
            case "ALLOW" -> "Device is secure";
            case "ALLOW_WITH_WARNING" -> "Device has minor security concerns";
            case "LIMITED_FEATURES" -> "Device has moderate security concerns, some features limited";
            case "BLOCK_ALL" -> "Device is not secure, access blocked";
            default -> "Unknown policy action";
        };

        return DeviceSecurityCheckResponse.builder()
            .deviceId(request.getDeviceId())
            .riskScore(riskScore)
            .riskLevel(riskLevel)
            .policyAction(policyAction)
            .message(message)
            .allowed(allowed)
            .build();
    }

    private int calculateRiskScore(DeviceSecurityCheckRequest request) {
        int score = 0;

        if (Boolean.TRUE.equals(request.getIsRooted())) score += 30;
        if (Boolean.TRUE.equals(request.getIsMockLocationEnabled())) score += 25;
        if (Boolean.TRUE.equals(request.getIsDeveloperMode())) score += 15;
        if (Boolean.FALSE.equals(request.getIsScreenLockEnabled())) score += 10;
        if (Boolean.TRUE.equals(request.getIsUnknownSources())) score += 10;
        if (Boolean.FALSE.equals(request.getIsPlayProtectEnabled())) score += 10;

        return Math.min(score, 100);
    }

    private String getRiskLevel(int score) {
        if (score >= 70) return "HIGH";
        if (score >= 40) return "MEDIUM";
        return "LOW";
    }

    private String getPolicyAction(int score) {
        if (score >= blockScoreThreshold) return "BLOCK_ALL";
        if (score >= limitedScoreThreshold) return "LIMITED_FEATURES";
        if (score >= warningScoreThreshold) return "ALLOW_WITH_WARNING";
        return "ALLOW";
    }
}
