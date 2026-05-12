package com.tracko.backend.repository;

import com.tracko.backend.model.CallReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CallReportRepository extends JpaRepository<CallReport, Long> {

    List<CallReport> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<CallReport> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<CallReport> findBySubmissionStatus(String submissionStatus);

    List<CallReport> findByVisitId(Long visitId);

    List<CallReport> findByReviewStatus(String reviewStatus);

    List<CallReport> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(cr) FROM CallReport cr WHERE cr.user.id = :userId " +
           "AND cr.createdAt BETWEEN :start AND :end")
    long countByUserIdAndCreatedAtBetween(@Param("userId") Long userId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    @Query("SELECT cr FROM CallReport cr WHERE cr.submissionStatus = 'PENDING' " +
           "AND cr.user.id = :userId")
    List<CallReport> findPendingByUserId(@Param("userId") Long userId);

    @Query("SELECT cr FROM CallReport cr WHERE cr.submissionStatus = 'DRAFT' " +
           "AND cr.user.id = :userId")
    List<CallReport> findUnsubmittedByUserId(@Param("userId") Long userId);

    Page<CallReport> findBySubmissionStatus(String submissionStatus, Pageable pageable);
}
