package com.tracko.backend.repository;

import com.tracko.backend.model.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByUserIdAndAttendanceDate(Long userId, LocalDate date);

    List<Attendance> findByUserIdAndAttendanceDateBetween(Long userId, LocalDate start, LocalDate end);

    Page<Attendance> findByUserIdOrderByAttendanceDateDesc(Long userId, Pageable pageable);

    List<Attendance> findByAttendanceDateBetween(LocalDate start, LocalDate end);

    List<Attendance> findByAttendanceDate(LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.user.managerId = :managerId AND a.attendanceDate = :date")
    List<Attendance> findByManagerIdAndDate(@Param("managerId") Long managerId,
                                            @Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.user.branchId = :branchId AND a.attendanceDate = :date")
    List<Attendance> findByBranchIdAndDate(@Param("branchId") Long branchId,
                                           @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.id = :userId " +
           "AND a.attendanceDate BETWEEN :start AND :end AND a.status = :status")
    long countByUserIdAndDateBetweenAndStatus(@Param("userId") Long userId,
                                              @Param("start") LocalDate start,
                                              @Param("end") LocalDate end,
                                              @Param("status") String status);

    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.attendanceDate = :date GROUP BY a.status")
    List<Object[]> getStatusSummaryByDate(@Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate = :date AND a.checkOutTime IS NULL " +
           "AND a.checkInTime IS NOT NULL")
    List<Attendance> findMissedCheckouts(@Param("date") LocalDate date);

    List<Attendance> findByUserIdAndAttendanceDateBetweenAndStatus(
        Long userId, LocalDate start, LocalDate end, String status);
}
