package com.tracko.backend.repository;

import com.tracko.backend.model.CustomerMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerMasterRepository extends JpaRepository<CustomerMaster, Long> {
    List<CustomerMaster> findByBranchId(Long branchId);
    List<CustomerMaster> findByAssignedTo(Long assignedTo);
    List<CustomerMaster> findByIsActiveTrue();
    Page<CustomerMaster> findByNameContainingIgnoreCase(String name, Pageable pageable);
    boolean existsByCustomerCode(String customerCode);
}
