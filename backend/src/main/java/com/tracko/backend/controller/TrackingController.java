package com.tracko.backend.controller;

import com.tracko.backend.dto.*;
import com.tracko.backend.security.CustomUserDetails;
import com.tracko.backend.service.TrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    @PostMapping("/location/batch")
    public ResponseEntity<ApiResponse<Integer>> uploadLocationBatch(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody LocationBatchRequest request) {
        int processed = trackingService.uploadLocationBatch(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(processed + " points processed", processed));
    }

    @GetMapping("/route")
    public ResponseEntity<ApiResponse<RouteResponse>> getRouteHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to) {
        RouteResponse route = trackingService.getRouteHistory(userDetails.getUserId(), from, to);
        return ResponseEntity.ok(ApiResponse.success(route));
    }

    @GetMapping("/last-known")
    public ResponseEntity<ApiResponse<LastKnownResponse>> getLastKnownLocation(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        LastKnownResponse location = trackingService.getLastKnownLocation(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(location));
    }

    @GetMapping("/distance")
    public ResponseEntity<ApiResponse<Double>> getDistanceTravelled(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to) {
        double distance = trackingService.getDistanceTravelled(userDetails.getUserId(), from, to);
        return ResponseEntity.ok(ApiResponse.success("Distance: " + distance + " km", distance));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Long>> getTrackingStats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to) {
        long stats = trackingService.getTrackingStats(userDetails.getUserId(), from, to);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/missed-pings")
    public ResponseEntity<ApiResponse<Integer>> getMissedPings(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        int missedPings = trackingService.getMissedPingsCount(
            userDetails.getUserId(), LocalDateTime.now().minusHours(24));
        return ResponseEntity.ok(ApiResponse.success(missedPings));
    }
}
