package com.tracko.backend.repository;

import com.tracko.backend.model.QuotationApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuotationApprovalRepository extends JpaRepository<QuotationApproval, Long> {
    List<QuotationApproval> findByQuotationIdOrderByCreatedAtDesc(Long quotationId);
}
