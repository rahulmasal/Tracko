package com.tracko.backend.scheduler;

import com.tracko.backend.model.Visit;
import com.tracko.backend.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportReminderJob implements Job {

    private final VisitRepository visitRepository;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Starting report reminder job");

        LocalDate today = LocalDate.now();
        List<Visit> completedVisits = visitRepository.findByUserIdAndPlannedDate(
            null, today);

        log.info("Report reminder job completed. Found {} visits needing reports", completedVisits.size());
    }
}
