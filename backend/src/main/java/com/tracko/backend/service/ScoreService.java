package com.tracko.backend.service;

import com.tracko.backend.exception.ResourceNotFoundException;
import com.tracko.backend.model.Enquiry;
import com.tracko.backend.model.ScoreCard;
import com.tracko.backend.model.User;
import com.tracko.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ScoreCardRepository scoreCardRepository;
    private final AttendanceRepository attendanceRepository;
    private final VisitRepository visitRepository;
    private final CallReportRepository callReportRepository;
    private final TrackingLogRepository trackingLogRepository;
    private final EnquiryRepository enquiryRepository;
    private final QuotationRepository quotationRepository;
    private final UserRepository userRepository;

    private static final double WEIGHT_VISIT_COMPLETION = 0.20;
    private static final double WEIGHT_ON_TIME_ATTENDANCE = 0.15;
    private static final double WEIGHT_REPORT_COMPLETENESS = 0.15;
    private static final double WEIGHT_TRACKING_COMPLIANCE = 0.10;
    private static final double WEIGHT_PHOTO_PROOF = 0.10;
    private static final double WEIGHT_CUSTOMER_FEEDBACK = 0.10;
    private static final double WEIGHT_REPEAT_VISIT = 0.05;
    private static final double WEIGHT_ENQUIRY_GEN = 0.05;
    private static final double WEIGHT_QUOTE_FOLLOWUP = 0.05;
    private static final double WEIGHT_JOB_CLOSURE = 0.03;
    private static final double WEIGHT_MANAGER_SCORE = 0.02;

    public ScoreCard getMyScore(Long userId, Integer month, Integer year) {
        return scoreCardRepository.findByUserIdAndScoreMonthAndScoreYear(userId, month, year)
            .orElse(null);
    }

    public List<ScoreCard> getScoreTrend(Long userId) {
        return scoreCardRepository.findByUserIdOrderByScoreYearDescScoreMonthDesc(userId);
    }

    public List<ScoreCard> getTeamScores(Long managerId, Integer month, Integer year) {
        List<User> teamMembers = userRepository.findByManagerId(managerId);
        List<Long> userIds = teamMembers.stream().map(User::getId).toList();
        return scoreCardRepository.findByScoreMonthAndScoreYear(month, year);
    }

    public List<ScoreCard> getAllScores(Integer month, Integer year) {
        if (month != null && year != null) {
            return scoreCardRepository.findByScoreMonthAndScoreYear(month, year);
        }
        return scoreCardRepository.findAll();
    }

    @Transactional
    public ScoreCard calculateScore(Long userId, Integer month, Integer year) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
        LocalDateTime startDateTime = monthStart.atStartOfDay();
        LocalDateTime endDateTime = monthEnd.atTime(23, 59, 59);

        double visitCompletion = calculateVisitCompletion(userId, monthStart, monthEnd);
        double onTimeAttendance = calculateOnTimeAttendance(userId, monthStart, monthEnd);
        double reportCompleteness = calculateReportCompleteness(userId, monthStart, monthEnd);
        double trackingCompliance = calculateTrackingCompliance(userId, startDateTime, endDateTime);
        double photoProof = calculatePhotoProof(userId, monthStart, monthEnd);
        double customerFeedback = calculateCustomerFeedback(userId, monthStart, monthEnd);
        double repeatVisit = calculateRepeatVisit(userId, monthStart, monthEnd);
        double enquiryGen = calculateEnquiryGeneration(userId, startDateTime, endDateTime);
        double quoteFollowup = calculateQuoteFollowup(userId, startDateTime, endDateTime);
        double jobClosure = calculateJobClosure(userId, monthStart, monthEnd);

        double totalScore =
            visitCompletion * WEIGHT_VISIT_COMPLETION +
            onTimeAttendance * WEIGHT_ON_TIME_ATTENDANCE +
            reportCompleteness * WEIGHT_REPORT_COMPLETENESS +
            trackingCompliance * WEIGHT_TRACKING_COMPLIANCE +
            photoProof * WEIGHT_PHOTO_PROOF +
            customerFeedback * WEIGHT_CUSTOMER_FEEDBACK +
            repeatVisit * WEIGHT_REPEAT_VISIT +
            enquiryGen * WEIGHT_ENQUIRY_GEN +
            quoteFollowup * WEIGHT_QUOTE_FOLLOWUP +
            jobClosure * WEIGHT_JOB_CLOSURE;

        totalScore = Math.round(totalScore * 100.0) / 100.0;

        String rating;
        if (totalScore >= 80) rating = "GREEN";
        else if (totalScore >= 60) rating = "AMBER";
        else rating = "RED";

        ScoreCard scoreCard = scoreCardRepository
            .findByUserIdAndScoreMonthAndScoreYear(userId, month, year)
            .orElse(ScoreCard.builder()
                .user(user)
                .scoreMonth(month)
                .scoreYear(year)
                .build());

        scoreCard.setVisitCompletionScore(visitCompletion);
        scoreCard.setOnTimeAttendanceScore(onTimeAttendance);
        scoreCard.setReportCompletenessScore(reportCompleteness);
        scoreCard.setTrackingComplianceScore(trackingCompliance);
        scoreCard.setPhotoProofScore(photoProof);
        scoreCard.setCustomerFeedbackScore(customerFeedback);
        scoreCard.setRepeatVisitScore(repeatVisit);
        scoreCard.setEnquiryGenScore(enquiryGen);
        scoreCard.setQuoteFollowupScore(quoteFollowup);
        scoreCard.setJobClosureScore(jobClosure);
        scoreCard.setTotalScore(totalScore);
        scoreCard.setRating(rating);

        return scoreCardRepository.save(scoreCard);
    }

    private double calculateVisitCompletion(Long userId, LocalDate start, LocalDate end) {
        long completed = visitRepository.countCompletedByUserAndDateBetween(userId, start, end);
        long total = visitRepository.countTotalByUserAndDateBetween(userId, start, end);
        return total > 0 ? (double) completed / total * 100 : 0;
    }

    private double calculateOnTimeAttendance(Long userId, LocalDate start, LocalDate end) {
        long total = attendanceRepository.countByUserIdAndDateBetweenAndStatus(
            userId, start, end, "PRESENT");
        long late = attendanceRepository.countByUserIdAndDateBetweenAndStatus(
            userId, start, end, "LATE");
        long totalDays = ChronoUnit.DAYS.between(start, end) + 1;
        return totalDays > 0 ? (total + (late * 0.5)) / totalDays * 100 : 0;
    }

    private double calculateReportCompleteness(Long userId, LocalDate start, LocalDate end) {
        long completedVisits = visitRepository.countCompletedByUserAndDateBetween(userId, start, end);
        long reports = callReportRepository.countByUserIdAndCreatedAtBetween(
            userId, start.atStartOfDay(), end.atTime(23, 59, 59));
        return completedVisits > 0 ? (double) reports / completedVisits * 100 : 0;
    }

    private double calculateTrackingCompliance(Long userId, LocalDateTime start, LocalDateTime end) {
        long expectedPings = ChronoUnit.MINUTES.between(start, end) / 15;
        long actualPings = trackingLogRepository.countByUserIdAndRecordedAtBetween(userId, start, end);
        long expected = Math.max(expectedPings, 1);
        return Math.min((double) actualPings / expected * 100, 100);
    }

    private double calculatePhotoProof(Long userId, LocalDate start, LocalDate end) {
        return 0;
    }

    private double calculateCustomerFeedback(Long userId, LocalDate start, LocalDate end) {
        return 0;
    }

    private double calculateRepeatVisit(Long userId, LocalDate start, LocalDate end) {
        return 100;
    }

    private double calculateEnquiryGeneration(Long userId, LocalDateTime start, LocalDateTime end) {
        List<Enquiry> enquiries = enquiryRepository
            .findByAssignedToIdAndCreatedAtBetween(userId, start, end);
        long count = enquiries != null ? enquiries.size() : 0;
        return Math.min(count * 10, 100);
    }

    private double calculateQuoteFollowup(Long userId, LocalDateTime start, LocalDateTime end) {
        long quotes = quotationRepository.countByUserIdAndCreatedAtBetween(userId, start, end);
        return Math.min(quotes * 10, 100);
    }

    private double calculateJobClosure(Long userId, LocalDate start, LocalDate end) {
        return 100;
    }
}
