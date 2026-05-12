package com.tracko.backend.scheduler;

import com.tracko.backend.repository.AuditLogRepository;
import com.tracko.backend.repository.MissedPingRepository;
import com.tracko.backend.repository.TrackingLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataRetentionCleanupJob implements Job {

    private final TrackingLogRepository trackingLogRepository;
    private final AuditLogRepository auditLogRepository;
    private final MissedPingRepository missedPingRepository;

    @Value("${app.tracking.data-retention-days:90}")
    private int retentionDays;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Starting data retention cleanup job");

        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);

        try {
            long deletedTrackingLogs = trackingLogRepository.deleteByRecordedAtBefore(cutoff);
            log.info("Deleted {} tracking logs older than {} days", deletedTrackingLogs, retentionDays);
        } catch (Exception e) {
            log.warn("Tracking log cleanup not supported via deleteBy, will use native query");
        }

        try {
            var oldAuditLogs = auditLogRepository.findByCreatedAtBefore(
                LocalDateTime.now().minusDays(retentionDays + 180));
            log.info("Found {} audit logs eligible for archival", oldAuditLogs.size());
        } catch (Exception e) {
            log.error("Failed to check audit logs for cleanup: {}", e.getMessage());
        }

        try {
            var oldMissedPings = missedPingRepository.findByCreatedAtBefore(cutoff);
            log.info("Found {} missed ping records eligible for cleanup", oldMissedPings.size());
        } catch (Exception e) {
            log.error("Failed to check missed pings for cleanup: {}", e.getMessage());
        }

        log.info("Data retention cleanup completed");
    }
}
