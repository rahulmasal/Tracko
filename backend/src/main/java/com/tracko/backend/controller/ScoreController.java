package com.tracko.backend.controller;

import com.tracko.backend.dto.ApiResponse;
import com.tracko.backend.model.ScoreCard;
import com.tracko.backend.security.CustomUserDetails;
import com.tracko.backend.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    @GetMapping("/my-score")
    public ResponseEntity<ApiResponse<ScoreCard>> getMyScore(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        LocalDate now = LocalDate.now();
        ScoreCard score = scoreService.getMyScore(
            userDetails.getUserId(), now.getMonthValue(), now.getYear());
        return ResponseEntity.ok(ApiResponse.success(score));
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ScoreCard>>> getTeamScores(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        LocalDate now = LocalDate.now();
        List<ScoreCard> scores = scoreService.getTeamScores(
            userDetails.getUserId(),
            month != null ? month : now.getMonthValue(),
            year != null ? year : now.getYear());
        return ResponseEntity.ok(ApiResponse.success(scores));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ScoreCard>>> getAllScores(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        List<ScoreCard> scores = scoreService.getAllScores(month, year);
        return ResponseEntity.ok(ApiResponse.success(scores));
    }

    @GetMapping("/trend")
    public ResponseEntity<ApiResponse<List<ScoreCard>>> getScoreTrend(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ScoreCard> trend = scoreService.getScoreTrend(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(trend));
    }

    @PostMapping("/recalculate/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ScoreCard>> recalculateScore(
            @PathVariable Long userId,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        ScoreCard score = scoreService.calculateScore(userId, month, year);
        return ResponseEntity.ok(ApiResponse.success("Score recalculated", score));
    }
}
