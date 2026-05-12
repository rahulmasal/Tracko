package com.tracko.backend.repository;

import com.tracko.backend.model.Enquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {

    Page<Enquiry> findByAssignedToIdOrderByCreatedAtDesc(Long assignedToId, Pageable pageable);

    Page<Enquiry> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    List<Enquiry> findByAssignedToIdAndCreatedAtBetween(Long assignedToId,
                                                        LocalDateTime start, LocalDateTime end);

    long countByStatus(String status);

    @Query("SELECT e.status, COUNT(e) FROM Enquiry e GROUP BY e.status")
    List<Object[]> getStatusCounts();

    @Query("SELECT e.source, COUNT(e) FROM Enquiry e GROUP BY e.source")
    List<Object[]> getSourceCounts();

    @Query("SELECT FUNCTION('DATE_TRUNC', 'month', e.createdAt), COUNT(e) " +
           "FROM Enquiry e WHERE e.createdAt >= :since GROUP BY FUNCTION('DATE_TRUNC', 'month', e.createdAt)")
    List<Object[]> getMonthlyTrend(@Param("since") LocalDateTime since);

    Page<Enquiry> findByCustomerNameContainingIgnoreCaseOrCustomerPhoneContaining(
        String customerName, String customerPhone, Pageable pageable);
}
