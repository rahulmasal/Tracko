package com.tracko.backend.scheduler;

import com.tracko.backend.model.LeaveBalance;
import com.tracko.backend.repository.LeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveBalanceResetJob implements Job {

    private final LeaveBalanceRepository leaveBalanceRepository;

    private static final Map<String, Double> DEFAULT_ALLOCATIONS = Map.of(
        "ANNUAL", 18.0,
        "SICK", 12.0,
        "PERSONAL", 6.0,
        "CASUAL", 6.0
    );

    private static final double MAX_CARRY_FORWARD = 5.0;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Starting leave balance reset job");

        int currentYear = LocalDate.now().getYear();
        int previousYear = currentYear - 1;

        List<LeaveBalance> previousYearBalances = leaveBalanceRepository.findByYear(previousYear);

        for (LeaveBalance prevBalance : previousYearBalances) {
            double carryForward = Math.min(prevBalance.getRemaining(), MAX_CARRY_FORWARD);

            LeaveBalance newBalance = leaveBalanceRepository
                .findByUserIdAndLeaveTypeAndYear(prevBalance.getUserId(), prevBalance.getLeaveType(), currentYear)
                .orElse(null);

            if (newBalance == null) {
                newBalance = LeaveBalance.builder()
                    .userId(prevBalance.getUserId())
                    .leaveType(prevBalance.getLeaveType())
                    .year(currentYear)
                    .totalAllocated(DEFAULT_ALLOCATIONS.getOrDefault(prevBalance.getLeaveType(), 0.0))
                    .used(0.0)
                    .pending(0.0)
                    .carriedForward(carryForward)
                    .build();
                leaveBalanceRepository.save(newBalance);
            }
        }

        log.info("Leave balance reset completed for year {}", currentYear);
    }
}
