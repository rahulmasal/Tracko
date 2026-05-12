package com.tracko.backend.scheduler;

import com.tracko.backend.model.MissedPing;
import com.tracko.backend.model.TrackingLog;
import com.tracko.backend.model.User;
import com.tracko.backend.repository.MissedPingRepository;
import com.tracko.backend.repository.TrackingLogRepository;
import com.tracko.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MissedPingDetectionJob implements Job {

    private final UserRepository userRepository;
    private final TrackingLogRepository trackingLogRepository;
    private final MissedPingRepository missedPingRepository;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Starting missed ping detection");

        List<User> activeUsers = userRepository.findByIsActiveTrue();
        LocalDateTime expectedPingAfter = LocalDateTime.now().minusMinutes(20);
        LocalDateTime thresholdTime = LocalDateTime.now().minusHours(1);

        int missedCount = 0;

        for (User user : activeUsers) {
            Optional<TrackingLog> lastLog = trackingLogRepository
                .findFirstByUserIdOrderByRecordedAtDesc(user.getId());

            if (lastLog.isEmpty() || lastLog.get().getRecordedAt().isBefore(expectedPingAfter)) {
                List<MissedPing> existingMisses = missedPingRepository
                    .findByUserIdAndIsResolvedFalse(user.getId());

                int consecutiveMisses = 1;
                if (!existingMisses.isEmpty()) {
                    consecutiveMisses = existingMisses.get(0).getConsecutiveMisses() + 1;
                }

                String severity = consecutiveMisses >= 6 ? "CRITICAL" :
                    consecutiveMisses >= 3 ? "HIGH" : "MEDIUM";

                MissedPing missedPing = MissedPing.builder()
                    .userId(user.getId())
                    .expectedTime(expectedPingAfter)
                    .lastKnownLat(lastLog.isPresent() ? lastLog.get().getLat() : null)
                    .lastKnownLng(lastLog.isPresent() ? lastLog.get().getLng() : null)
                    .lastKnownTime(lastLog.isPresent() ? lastLog.get().getRecordedAt() : null)
                    .consecutiveMisses(consecutiveMisses)
                    .severity(severity)
                    .build();

                missedPingRepository.save(missedPing);
                missedCount++;

                if (consecutiveMisses >= 6) {
                    log.warn("Critical: User {} missed {} consecutive pings", user.getId(), consecutiveMisses);
                }
            }
        }

        log.info("Missed ping detection completed. Found {} missed pings", missedCount);
    }
}
