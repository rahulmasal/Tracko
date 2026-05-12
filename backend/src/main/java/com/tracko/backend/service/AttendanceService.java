package com.tracko.backend.service;

import com.tracko.backend.dto.CheckInRequest;
import com.tracko.backend.dto.CheckOutRequest;
import com.tracko.backend.exception.BusinessException;
import com.tracko.backend.exception.ResourceNotFoundException;
import com.tracko.backend.model.Attendance;
import com.tracko.backend.model.AttendanceCorrection;
import com.tracko.backend.model.Shift;
import com.tracko.backend.model.User;
import com.tracko.backend.repository.AttendanceCorrectionRepository;
import com.tracko.backend.repository.AttendanceRepository;
import com.tracko.backend.repository.ShiftRepository;
import com.tracko.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceCorrectionRepository correctionRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;

    @Transactional
    public Attendance checkIn(Long userId, CheckInRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        LocalDate today = LocalDate.now();
        attendanceRepository.findByUserIdAndAttendanceDate(userId, today)
            .ifPresent(a -> {
                throw new BusinessException("Already checked in today");
            });

        LocalTime now = LocalTime.now();
        Shift shift = user.getShiftId() != null ?
            shiftRepository.findById(user.getShiftId()).orElse(null) : null;

        String status = calculateCheckInStatus(now, shift);
        int lateMinutes = 0;
        if ("LATE".equals(status) && shift != null) {
            lateMinutes = (int) ChronoUnit.MINUTES.between(shift.getStartTime(), now);
            if (lateMinutes < 0) lateMinutes = 0;
        }

        Attendance attendance = Attendance.builder()
            .user(user)
            .attendanceDate(today)
            .checkInTime(now)
            .checkInLat(request.getLat())
            .checkInLng(request.getLng())
            .checkInLocation(request.getLocation())
            .checkInPhotoUrl(request.getPhoto())
            .deviceInfo(request.getDeviceInfo())
            .ipAddress(request.getIpAddress())
            .status(status)
            .isLate("LATE".equals(status))
            .lateMinutes(lateMinutes)
            .geofenceVerified(false)
            .shiftStartTime(shift != null ? shift.getStartTime() : null)
            .shiftEndTime(shift != null ? shift.getEndTime() : null)
            .graceMinutes(shift != null ? shift.getGraceMinutes() : 15)
            .build();

        return attendanceRepository.save(attendance);
    }

    @Transactional
    public Attendance checkOut(Long userId, Long attendanceId, CheckOutRequest request) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance", "id", attendanceId));

        if (!attendance.getUser().getId().equals(userId)) {
            throw new BusinessException("Attendance record does not belong to this user");
        }

        if (attendance.getCheckOutTime() != null) {
            throw new BusinessException("Already checked out");
        }

        LocalTime now = LocalTime.now();
        attendance.setCheckOutTime(now);
        attendance.setCheckOutLat(request.getLat());
        attendance.setCheckOutLng(request.getLng());
        attendance.setCheckOutLocation(request.getLocation());
        attendance.setCheckOutPhotoUrl(request.getPhoto());

        double totalHours = ChronoUnit.MINUTES.between(
            attendance.getCheckInTime(), now) / 60.0;
        attendance.setTotalWorkingHours(totalHours);

        if (attendance.getShiftEndTime() != null && now.isAfter(attendance.getShiftEndTime())) {
            int overtime = (int) ChronoUnit.MINUTES.between(attendance.getShiftEndTime(), now);
            attendance.setIsOvertime(true);
            attendance.setOvertimeMinutes(overtime);
        }

        return attendanceRepository.save(attendance);
    }

    public Attendance getTodayAttendance(Long userId) {
        return attendanceRepository.findByUserIdAndAttendanceDate(userId, LocalDate.now())
            .orElse(null);
    }

    public Page<Attendance> getAttendanceHistory(Long userId, Pageable pageable) {
        return attendanceRepository.findByUserIdOrderByAttendanceDateDesc(userId, pageable);
    }

    public List<Attendance> getTeamAttendance(Long managerId, LocalDate date) {
        return attendanceRepository.findByManagerIdAndDate(managerId, date);
    }

    public Map<String, Long> getDailySummary(LocalDate date) {
        List<Object[]> summary = attendanceRepository.getStatusSummaryByDate(date);
        Map<String, Long> result = new HashMap<>();
        for (Object[] row : summary) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    public List<Attendance> getMonthlySummary(Long userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return attendanceRepository.findByUserIdAndAttendanceDateBetween(userId, start, end);
    }

    @Transactional
    public AttendanceCorrection requestCorrection(Long userId, Long attendanceId,
                                                   String correctionType, String reason,
                                                   String requestedValue) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance", "id", attendanceId));

        AttendanceCorrection correction = AttendanceCorrection.builder()
            .user(attendance.getUser())
            .attendance(attendance)
            .correctionType(correctionType)
            .reason(reason)
            .originalValue(attendance.getStatus())
            .requestedValue(requestedValue)
            .status("PENDING")
            .build();

        return correctionRepository.save(correction);
    }

    public List<AttendanceCorrection> getPendingCorrections() {
        return correctionRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    @Transactional
    public AttendanceCorrection reviewCorrection(Long correctionId, String action, String comments,
                                                  Long reviewerId) {
        AttendanceCorrection correction = correctionRepository.findById(correctionId)
            .orElseThrow(() -> new ResourceNotFoundException("Correction", "id", correctionId));

        correction.setStatus(action.equalsIgnoreCase("APPROVE") ? "APPROVED" : "REJECTED");
        correction.setReviewComments(comments);
        correction.setReviewedBy(userRepository.getReferenceById(reviewerId));
        correction.setReviewedAt(LocalDateTime.now());

        if ("APPROVE".equalsIgnoreCase(action)) {
            Attendance attendance = correction.getAttendance();
            attendance.setStatusReason(correction.getRequestedValue());
            attendanceRepository.save(attendance);
        }

        return correctionRepository.save(correction);
    }

    public List<Attendance> findMissedCheckouts(LocalDate date) {
        return attendanceRepository.findMissedCheckouts(date);
    }

    private String calculateCheckInStatus(LocalTime checkInTime, Shift shift) {
        if (shift == null) return "PRESENT";
        LocalTime graceEnd = shift.getStartTime().plusMinutes(
            shift.getGraceMinutes() != null ? shift.getGraceMinutes() : 15);
        LocalTime lateThreshold = shift.getStartTime().plusMinutes(
            shift.getLateThresholdMinutes() != null ? shift.getLateThresholdMinutes() : 30);
        LocalTime halfDayThreshold = shift.getStartTime().plusMinutes(
            shift.getHalfDayThresholdMinutes() != null ? shift.getHalfDayThresholdMinutes() : 120);

        if (checkInTime.isBefore(shift.getStartTime()) || checkInTime.equals(shift.getStartTime())) {
            return "PRESENT";
        } else if (checkInTime.isBefore(graceEnd) || checkInTime.equals(graceEnd)) {
            return "PRESENT";
        } else if (checkInTime.isBefore(lateThreshold)) {
            return "LATE";
        } else if (checkInTime.isBefore(halfDayThreshold)) {
            return "HALF_DAY";
        } else {
            return "ABSENT";
        }
    }
}
