package com.tracko.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device_security_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceSecurityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "device_id", length = 255)
    private String deviceId;

    @Column(name = "os_version", length = 50)
    private String osVersion;

    @Column(name = "app_version", length = 20)
    private String appVersion;

    @Column(name = "device_model", length = 100)
    private String deviceModel;

    @Column(name = "is_rooted")
    private Boolean isRooted = false;

    @Column(name = "is_mock_location_enabled")
    private Boolean isMockLocationEnabled = false;

    @Column(name = "is_developer_mode")
    private Boolean isDeveloperMode = false;

    @Column(name = "is_screen_lock_enabled")
    private Boolean isScreenLockEnabled = true;

    @Column(name = "is_play_protect_enabled")
    private Boolean isPlayProtectEnabled = true;

    @Column(name = "is_unknown_sources")
    private Boolean isUnknownSources = false;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(length = 20)
    private String riskLevel;

    @Column(name = "policy_action", length = 30)
    private String policyAction;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (checkedAt == null) checkedAt = LocalDateTime.now();
    }
}
