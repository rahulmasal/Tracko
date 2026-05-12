package com.tracko.backend.service;

import com.tracko.backend.dto.CallReportRequest;
import com.tracko.backend.exception.BusinessException;
import com.tracko.backend.exception.ResourceNotFoundException;
import com.tracko.backend.model.*;
import com.tracko.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CallReportService {

    private final CallReportRepository callReportRepository;
    private final UserRepository userRepository;
    private final VisitRepository visitRepository;

    @Transactional
    public CallReport createReport(Long userId, CallReportRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Visit visit = request.getVisitId() != null ?
            visitRepository.findById(request.getVisitId()).orElse(null) : null;

        CallReport report = CallReport.builder()
            .user(user)
            .customerId(request.getCustomerId())
            .visit(visit)
            .reportDate(request.getReportDate() != null ? request.getReportDate() : LocalDateTime.now())
            .description(request.getDescription())
            .workDone(request.getWorkDone())
            .partsUsed(request.getPartsUsed())
            .recommendations(request.getRecommendations())
            .status(request.getStatus())
            .submissionStatus("DRAFT")
            .customerName(request.getCustomerName())
            .customerPhone(request.getCustomerPhone())
            .jobStartTime(request.getJobStartTime())
            .jobEndTime(request.getJobEndTime())
            .build();

        if (request.getJobStartTime() != null && request.getJobEndTime() != null) {
            double hours = ChronoUnit.MINUTES.between(request.getJobStartTime(), request.getJobEndTime()) / 60.0;
            report.setTotalHours(Math.round(hours * 100.0) / 100.0);
        }

        return callReportRepository.save(report);
    }

    @Transactional
    public CallReport updateReport(Long id, Long userId, CallReportRequest request) {
        CallReport report = callReportRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CallReport", "id", id));

        if (!report.getUser().getId().equals(userId)) {
            throw new BusinessException("Not authorized to update this report");
        }

        if (request.getCustomerId() != null) report.setCustomerId(request.getCustomerId());
        if (request.getDescription() != null) report.setDescription(request.getDescription());
        if (request.getWorkDone() != null) report.setWorkDone(request.getWorkDone());
        if (request.getPartsUsed() != null) report.setPartsUsed(request.getPartsUsed());
        if (request.getRecommendations() != null) report.setRecommendations(request.getRecommendations());
        if (request.getStatus() != null) report.setStatus(request.getStatus());
        if (request.getCustomerName() != null) report.setCustomerName(request.getCustomerName());
        if (request.getCustomerPhone() != null) report.setCustomerPhone(request.getCustomerPhone());
        if (request.getJobStartTime() != null) report.setJobStartTime(request.getJobStartTime());
        if (request.getJobEndTime() != null) report.setJobEndTime(request.getJobEndTime());

        if (report.getJobStartTime() != null && report.getJobEndTime() != null) {
            double hours = ChronoUnit.MINUTES.between(report.getJobStartTime(), report.getJobEndTime()) / 60.0;
            report.setTotalHours(Math.round(hours * 100.0) / 100.0);
        }

        return callReportRepository.save(report);
    }

    @Transactional
    public CallReport submitReport(Long id, Long userId) {
        CallReport report = callReportRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CallReport", "id", id));

        if (!report.getUser().getId().equals(userId)) {
            throw new BusinessException("Not authorized to submit this report");
        }

        report.setSubmissionStatus("SUBMITTED");
        report.setSubmittedAt(LocalDateTime.now());
        return callReportRepository.save(report);
    }

    public CallReport getReport(Long id) {
        return callReportRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CallReport", "id", id));
    }

    @Transactional
    public CallReport reviewReport(Long id, String action, String comments, Long reviewerId) {
        CallReport report = callReportRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CallReport", "id", id));

        report.setReviewStatus(action.equalsIgnoreCase("APPROVE") ? "APPROVED" : "REWORK");
        report.setReviewedBy(reviewerId);
        report.setReviewedAt(LocalDateTime.now());
        report.setReviewComments(comments);

        if ("REWORK".equalsIgnoreCase(action)) {
            report.setSubmissionStatus("DRAFT");
        }

        return callReportRepository.save(report);
    }

    public Page<CallReport> getReportHistory(Long userId, Pageable pageable) {
        return callReportRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public List<CallReport> getPendingReports() {
        return callReportRepository.findBySubmissionStatus("SUBMITTED");
    }

    public List<CallReport> getUnsubmittedReports(Long userId) {
        return callReportRepository.findUnsubmittedByUserId(userId);
    }
}
