package com.tracko.backend.repository;

import com.tracko.backend.model.Quotation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    Page<Quotation> findByCreatedByIdOrderByCreatedAtDesc(Long createdById, Pageable pageable);

    List<Quotation> findByApprovalStatus(String approvalStatus);

    Page<Quotation> findByApprovalStatus(String approvalStatus, Pageable pageable);

    List<Quotation> findBySlaDeadlineBeforeAndSlaBreached(LocalDateTime deadline, boolean breached);

    @Query("SELECT q FROM Quotation q WHERE q.slaDeadline IS NOT NULL " +
           "AND q.slaDeadline <= :deadline AND q.slaBreached = false " +
           "AND q.approvalStatus NOT IN ('APPROVED', 'REJECTED')")
    List<Quotation> findNearingSlaBreach(@Param("deadline") LocalDateTime deadline);

    @Query("SELECT COUNT(q) FROM Quotation q WHERE q.createdById = :userId " +
           "AND q.createdAt BETWEEN :start AND :end")
    long countByUserIdAndCreatedAtBetween(@Param("userId") Long userId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    long countByApprovalStatus(String approvalStatus);
}
