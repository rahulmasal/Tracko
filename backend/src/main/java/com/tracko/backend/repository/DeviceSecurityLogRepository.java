package com.tracko.backend.repository;

import com.tracko.backend.model.DeviceSecurityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceSecurityLogRepository extends JpaRepository<DeviceSecurityLog, Long> {

    List<DeviceSecurityLog> findByUserIdOrderByCheckedAtDesc(Long userId);

    Page<DeviceSecurityLog> findByRiskLevelOrderByCheckedAtDesc(String riskLevel, Pageable pageable);

    List<DeviceSecurityLog> findByPolicyAction(String policyAction);
}
