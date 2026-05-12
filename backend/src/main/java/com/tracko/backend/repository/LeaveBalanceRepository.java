package com.tracko.backend.repository;

import com.tracko.backend.model.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    List<LeaveBalance> findByUserIdAndYear(Long userId, Integer year);

    Optional<LeaveBalance> findByUserIdAndLeaveTypeAndYear(Long userId, String leaveType, Integer year);

    List<LeaveBalance> findByYear(Integer year);
}
