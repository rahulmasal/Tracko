package com.tracko.backend.service;

import com.tracko.backend.dto.LastKnownResponse;
import com.tracko.backend.dto.LocationBatchRequest;
import com.tracko.backend.dto.LocationPoint;
import com.tracko.backend.dto.RouteResponse;
import com.tracko.backend.exception.BusinessException;
import com.tracko.backend.model.MissedPing;
import com.tracko.backend.model.TrackingLog;
import com.tracko.backend.repository.MissedPingRepository;
import com.tracko.backend.repository.TrackingLogRepository;
import com.tracko.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingService {

    private final TrackingLogRepository trackingLogRepository;
    private final MissedPingRepository missedPingRepository;
    private final UserRepository userRepository;

    private static final double EARTH_RADIUS_KM = 6371.0;

    @Transactional
    public int uploadLocationBatch(Long userId, LocationBatchRequest request) {
        List<TrackingLog> logs = new ArrayList<>();
        for (LocationPoint point : request.getPoints()) {
            if (point.getLat() < -90 || point.getLat() > 90 ||
                point.getLng() < -180 || point.getLng() > 180) {
                log.warn("Invalid coordinates: lat={}, lng={}", point.getLat(), point.getLng());
                continue;
            }
            if (point.getRecordedAt().isAfter(LocalDateTime.now())) {
                log.warn("Future timestamp ignored: {}", point.getRecordedAt());
                continue;
            }

            TrackingLog log = TrackingLog.builder()
                .userId(userId)
                .lat(point.getLat())
                .lng(point.getLng())
                .altitude(point.getAltitude())
                .accuracy(point.getAccuracy())
                .speed(point.getSpeed())
                .bearing(point.getBearing())
                .recordedAt(point.getRecordedAt())
                .batteryLevel(point.getBatteryLevel())
                .provider(point.getProvider())
                .isMocked(point.getIsMocked() != null && point.getIsMocked())
                .activityType(point.getActivityType())
                .activityConfidence(point.getActivityConfidence())
                .build();
            logs.add(log);
        }

        if (logs.isEmpty()) {
            return 0;
        }

        trackingLogRepository.saveAll(logs);
        return logs.size();
    }

    public RouteResponse getRouteHistory(Long userId, LocalDateTime from, LocalDateTime to) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<TrackingLog> logs = trackingLogRepository
            .findByUserIdAndRecordedAtBetweenOrderByRecordedAtAsc(userId, from, to);

        double totalDistance = 0;
        List<RouteResponse.RoutePoint> points = new ArrayList<>();
        TrackingLog prev = null;

        for (TrackingLog log : logs) {
            if (prev != null) {
                totalDistance += haversineDistance(prev.getLat(), prev.getLng(),
                    log.getLat(), log.getLng());
            }
            points.add(RouteResponse.RoutePoint.builder()
                .lat(log.getLat())
                .lng(log.getLng())
                .recordedAt(log.getRecordedAt())
                .speed(log.getSpeed())
                .accuracy(log.getAccuracy())
                .build());
            prev = log;
        }

        return RouteResponse.builder()
            .userId(userId)
            .userName(user.getFullName())
            .fromDate(from)
            .toDate(to)
            .points(points)
            .totalDistanceKm(Math.round(totalDistance * 100.0) / 100.0)
            .totalPoints(points.size())
            .build();
    }

    public LastKnownResponse getLastKnownLocation(Long userId) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<TrackingLog> lastLog = trackingLogRepository
            .findFirstByUserIdOrderByRecordedAtDesc(userId);

        if (lastLog.isEmpty()) {
            return null;
        }

        TrackingLog log = lastLog.get();
        int minutesSince = (int) ChronoUnit.MINUTES.between(log.getRecordedAt(), LocalDateTime.now());

        return LastKnownResponse.builder()
            .userId(userId)
            .userName(user.getFullName())
            .lat(log.getLat())
            .lng(log.getLng())
            .recordedAt(log.getRecordedAt())
            .speed(log.getSpeed())
            .accuracy(log.getAccuracy())
            .batteryLevel(log.getBatteryLevel())
            .minutesSinceUpdate(minutesSince)
            .build();
    }

    public double getDistanceTravelled(Long userId, LocalDateTime from, LocalDateTime to) {
        List<TrackingLog> logs = trackingLogRepository
            .findByUserIdAndRecordedAtBetweenOrderByRecordedAtAsc(userId, from, to);

        double totalDistance = 0;
        TrackingLog prev = null;
        for (TrackingLog log : logs) {
            if (prev != null) {
                totalDistance += haversineDistance(prev.getLat(), prev.getLng(),
                    log.getLat(), log.getLng());
            }
            prev = log;
        }
        return Math.round(totalDistance * 100.0) / 100.0;
    }

    public long getTrackingStats(Long userId, LocalDateTime from, LocalDateTime to) {
        return trackingLogRepository.countByUserIdAndRecordedAtBetween(userId, from, to);
    }

    public int getMissedPingsCount(Long userId, LocalDateTime since) {
        return missedPingRepository.findByUserIdAndIsResolvedFalse(userId).size();
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
