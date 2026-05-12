package com.tracko.backend.repository;

import com.tracko.backend.model.AttendanceCorrection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceCorrectionRepository extends JpaRepository<AttendanceCorrection, Long> {
    List<AttendanceCorrection> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<AttendanceCorrection> findByStatusOrderByCreatedAtDesc(String status);
    List<AttendanceCorrection> findByAttendanceId(Long attendanceId);
}
