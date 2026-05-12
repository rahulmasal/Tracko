package com.tracko.backend.repository;

import com.tracko.backend.model.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    Page<LeaveRequest> findByUserIdOrderByAppliedAtDesc(Long userId, Pageable pageable);

    List<LeaveRequest> findByUserIdAndStartDateBetween(Long userId,
                                                       LocalDate start, LocalDate end);

    List<LeaveRequest> findByUserIdAndStatus(Long userId, String status);

    Page<LeaveRequest> findByStatusOrderByAppliedAtDesc(String status, Pageable pageable);

    List<LeaveRequest> findByStatus(String status);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.user.managerId = :managerId " +
           "AND lr.status = 'PENDING' ORDER BY lr.appliedAt DESC")
    List<LeaveRequest> findPendingLeavesByManagerId(@Param("managerId") Long managerId);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.status = 'APPROVED' " +
           "AND lr.startDate <= :date AND lr.endDate >= :date")
    List<LeaveRequest> findApprovedLeavesOnDate(@Param("date") LocalDate date);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.user.id = :userId " +
           "AND lr.status = 'APPROVED' " +
           "AND ((lr.startDate BETWEEN :start AND :end) OR (lr.endDate BETWEEN :start AND :end) " +
           "OR (lr.startDate <= :start AND lr.endDate >= :end))")
    List<LeaveRequest> findConflictingLeaves(@Param("userId") Long userId,
                                             @Param("start") LocalDate start,
                                             @Param("end") LocalDate end);
}
