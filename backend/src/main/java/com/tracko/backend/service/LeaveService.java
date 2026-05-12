package com.tracko.backend.service;

import com.tracko.backend.dto.LeaveRequestDto;
import com.tracko.backend.exception.BusinessException;
import com.tracko.backend.exception.ResourceNotFoundException;
import com.tracko.backend.model.LeaveBalance;
import com.tracko.backend.model.LeaveRequest;
import com.tracko.backend.model.User;
import com.tracko.backend.repository.HolidayRepository;
import com.tracko.backend.repository.LeaveBalanceRepository;
import com.tracko.backend.repository.LeaveRequestRepository;
import com.tracko.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final HolidayRepository holidayRepository;
    private final UserRepository userRepository;

    private static final int MAX_CONSECUTIVE_DAYS = 15;

    @Transactional
    public LeaveRequest applyLeave(Long userId, LeaveRequestDto request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Cannot apply leave for past dates");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        long daysBetween = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        if (request.getIsHalfDay() != null && request.getIsHalfDay()) {
            daysBetween = 0.5;
        }
        if (daysBetween > MAX_CONSECUTIVE_DAYS) {
            throw new BusinessException("Cannot apply more than " + MAX_CONSECUTIVE_DAYS + " consecutive days");
        }

        checkLeaveBalance(userId, request.getType(), (double) daysBetween);

        List<LeaveRequest> conflicts = leaveRequestRepository.findConflictingLeaves(
            userId, request.getStartDate(), request.getEndDate());
        if (!conflicts.isEmpty()) {
            throw new BusinessException("Leave already exists for this period");
        }

        LeaveBalance balance = leaveBalanceRepository
            .findByUserIdAndLeaveTypeAndYear(userId, request.getType(), LocalDate.now().getYear())
            .orElseThrow(() -> new BusinessException("Leave balance not found for type: " + request.getType()));

        LeaveRequest leaveRequest = LeaveRequest.builder()
            .user(user)
            .type(request.getType())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .totalDays((double) daysBetween)
            .reason(request.getReason())
            .isHalfDay(request.getIsHalfDay() != null && request.getIsHalfDay())
            .status("PENDING")
            .build();

        balance.setPending(balance.getPending() + daysBetween);
        leaveBalanceRepository.save(balance);

        return leaveRequestRepository.save(leaveRequest);
    }

    public LeaveRequest getLeave(Long id) {
        return leaveRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", "id", id));
    }

    @Transactional
    public LeaveRequest cancelLeave(Long id, Long userId) {
        LeaveRequest leave = getLeave(id);
        if (!leave.getUser().getId().equals(userId)) {
            throw new BusinessException("Not authorized to cancel this leave");
        }
        if (!"PENDING".equals(leave.getStatus())) {
            throw new BusinessException("Only pending leaves can be cancelled");
        }

        leave.setStatus("CANCELLED");

        LeaveBalance balance = leaveBalanceRepository
            .findByUserIdAndLeaveTypeAndYear(userId, leave.getType(), leave.getStartDate().getYear())
            .orElse(null);
        if (balance != null) {
            balance.setPending(balance.getPending() - leave.getTotalDays());
            leaveBalanceRepository.save(balance);
        }

        return leaveRequestRepository.save(leave);
    }

    @Transactional
    public LeaveRequest approveLeave(Long id, Long approverId, String comments) {
        LeaveRequest leave = getLeave(id);
        leave.setStatus("APPROVED");
        leave.setApprovedBy(userRepository.getReferenceById(approverId));
        leave.setApprovedAt(LocalDateTime.now());

        LeaveBalance balance = leaveBalanceRepository
            .findByUserIdAndLeaveTypeAndYear(leave.getUser().getId(), leave.getType(), leave.getStartDate().getYear())
            .orElse(null);
        if (balance != null) {
            balance.setPending(balance.getPending() - leave.getTotalDays());
            balance.setUsed(balance.getUsed() + leave.getTotalDays());
            leaveBalanceRepository.save(balance);
        }

        return leaveRequestRepository.save(leave);
    }

    @Transactional
    public LeaveRequest rejectLeave(Long id, Long approverId, String comments) {
        LeaveRequest leave = getLeave(id);
        leave.setStatus("REJECTED");
        leave.setApprovedBy(userRepository.getReferenceById(approverId));
        leave.setApprovedAt(LocalDateTime.now());
        leave.setRejectionReason(comments);

        LeaveBalance balance = leaveBalanceRepository
            .findByUserIdAndLeaveTypeAndYear(leave.getUser().getId(), leave.getType(), leave.getStartDate().getYear())
            .orElse(null);
        if (balance != null) {
            balance.setPending(balance.getPending() - leave.getTotalDays());
            leaveBalanceRepository.save(balance);
        }

        return leaveRequestRepository.save(leave);
    }

    public Page<LeaveRequest> getLeaveHistory(Long userId, Pageable pageable) {
        return leaveRequestRepository.findByUserIdOrderByAppliedAtDesc(userId, pageable);
    }

    public List<LeaveBalance> getLeaveBalance(Long userId) {
        int year = LocalDate.now().getYear();
        return leaveBalanceRepository.findByUserIdAndYear(userId, year);
    }

    public Page<LeaveRequest> getPendingLeaves(Pageable pageable) {
        return leaveRequestRepository.findByStatusOrderByAppliedAtDesc("PENDING", pageable);
    }

    public List<LeaveRequest> getTeamConflicts(Long managerId) {
        return leaveRequestRepository.findPendingLeavesByManagerId(managerId);
    }

    public Map<String, Object> getCalendar(Long userId, int year, int month) {
        Map<String, Object> calendar = new HashMap<>();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<LeaveRequest> leaves = leaveRequestRepository
            .findByUserIdAndStartDateBetween(userId, start, end);
        var holidays = holidayRepository.findByHolidayDateBetween(start, end);

        calendar.put("leaves", leaves);
        calendar.put("holidays", holidays);
        return calendar;
    }

    @Transactional
    public void initializeLeaveBalances(Long userId) {
        int year = LocalDate.now().getYear();
        Map<String, Double> defaultAllocations = Map.of(
            "ANNUAL", 18.0,
            "SICK", 12.0,
            "PERSONAL", 6.0,
            "CASUAL", 6.0
        );

        for (Map.Entry<String, Double> entry : defaultAllocations.entrySet()) {
            if (leaveBalanceRepository.findByUserIdAndLeaveTypeAndYear(userId, entry.getKey(), year).isEmpty()) {
                LeaveBalance balance = LeaveBalance.builder()
                    .userId(userId)
                    .leaveType(entry.getKey())
                    .year(year)
                    .totalAllocated(entry.getValue())
                    .used(0.0)
                    .pending(0.0)
                    .carriedForward(0.0)
                    .build();
                leaveBalanceRepository.save(balance);
            }
        }
    }

    private void checkLeaveBalance(Long userId, String leaveType, Double days) {
        int year = LocalDate.now().getYear();
        LeaveBalance balance = leaveBalanceRepository
            .findByUserIdAndLeaveTypeAndYear(userId, leaveType, year)
            .orElseThrow(() -> new BusinessException("Leave type not available: " + leaveType));

        if (balance.getRemaining() < days) {
            throw new BusinessException("Insufficient leave balance. Available: " +
                balance.getRemaining() + ", Requested: " + days);
        }
    }
}
