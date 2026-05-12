package com.tracko.backend.scheduler;

import com.tracko.backend.model.User;
import com.tracko.backend.repository.UserRepository;
import com.tracko.backend.service.ScoreService;
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
public class ScoreCalculationJob implements Job {

    private final UserRepository userRepository;
    private final ScoreService scoreService;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Starting monthly score calculation job");

        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        int month = lastMonth.getMonthValue();
        int year = lastMonth.getYear();

        List<User> activeUsers = userRepository.findByIsActiveTrue();
        int calculated = 0;

        for (User user : activeUsers) {
            try {
                scoreService.calculateScore(user.getId(), month, year);
                calculated++;
            } catch (Exception e) {
                log.error("Failed to calculate score for user {}: {}", user.getId(), e.getMessage());
            }
        }

        log.info("Score calculation completed. Calculated {} scores for {}/{}", calculated, month, year);
    }
}
