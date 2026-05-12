package com.tracko.backend.repository;

import com.tracko.backend.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    List<Holiday> findByHolidayDateBetween(LocalDate start, LocalDate end);
    List<Holiday> findByBranchIdAndHolidayDateBetween(Long branchId, LocalDate start, LocalDate end);
    List<Holiday> findByIsActiveTrue();
    boolean existsByHolidayDate(LocalDate date);
}
