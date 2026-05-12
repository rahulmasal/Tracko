package com.tracko.backend.service;

import com.tracko.backend.dto.DeviceSecurityCheckRequest;
import com.tracko.backend.dto.DeviceSecurityCheckResponse;
import com.tracko.backend.model.DeviceSecurityLog;
import com.tracko.backend.repository.DeviceSecurityLogRepository;
import com.tracko.backend.security.DeviceSecurityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceSecurityService {

    private final DeviceSecurityValidator securityValidator;
    private final DeviceSecurityLogRepository securityLogRepository;

    public DeviceSecurityCheckResponse checkDevice(DeviceSecurityCheckRequest request, Long userId) {
        return securityValidator.validate(request, userId);
    }

    public List<DeviceSecurityLog> getDeviceHistory(Long userId) {
        return securityLogRepository.findByUserIdOrderByCheckedAtDesc(userId);
    }

    public Page<DeviceSecurityLog> getHighRiskEvents(Pageable pageable) {
        return securityLogRepository.findByRiskLevelOrderByCheckedAtDesc("HIGH", pageable);
    }
}
