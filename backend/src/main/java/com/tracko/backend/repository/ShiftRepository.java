package com.tracko.backend.repository;

import com.tracko.backend.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByIsActiveTrue();
    List<Shift> findByBranchId(Long branchId);
}
