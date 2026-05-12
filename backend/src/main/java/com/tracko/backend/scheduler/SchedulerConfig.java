package com.tracko.backend.scheduler;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulerConfig {

    @Bean
    public JobDetail missedPingDetectionJob() {
        return JobBuilder.newJob(MissedPingDetectionJob.class)
            .withIdentity("missedPingDetectionJob")
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger missedPingDetectionTrigger() {
        return TriggerBuilder.newTrigger()
            .forJob(missedPingDetectionJob())
            .withIdentity("missedPingDetectionTrigger")
            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(15)
                .repeatForever())
            .build();
    }

    @Bean
    public JobDetail reportReminderJob() {
        return JobBuilder.newJob(ReportReminderJob.class)
            .withIdentity("reportReminderJob")
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger reportReminderTrigger() {
        return TriggerBuilder.newTrigger()
            .forJob(reportReminderJob())
            .withIdentity("reportReminderTrigger")
            .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(20, 0))
            .build();
    }

    @Bean
    public JobDetail leaveBalanceResetJob() {
        return JobBuilder.newJob(LeaveBalanceResetJob.class)
            .withIdentity("leaveBalanceResetJob")
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger leaveBalanceResetTrigger() {
        return TriggerBuilder.newTrigger()
            .forJob(leaveBalanceResetJob())
            .withIdentity("leaveBalanceResetTrigger")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 1 1 ? *"))
            .build();
    }

    @Bean
    public JobDetail scoreCalculationJob() {
        return JobBuilder.newJob(ScoreCalculationJob.class)
            .withIdentity("scoreCalculationJob")
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger scoreCalculationTrigger() {
        return TriggerBuilder.newTrigger()
            .forJob(scoreCalculationJob())
            .withIdentity("scoreCalculationTrigger")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 1 * ? *"))
            .build();
    }

    @Bean
    public JobDetail quotaSlaCheckJob() {
        return JobBuilder.newJob(QuotaSlaCheckJob.class)
            .withIdentity("quotaSlaCheckJob")
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger quotaSlaCheckTrigger() {
        return TriggerBuilder.newTrigger()
            .forJob(quotaSlaCheckJob())
            .withIdentity("quotaSlaCheckTrigger")
            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInHours(1)
                .repeatForever())
            .build();
    }

    @Bean
    public JobDetail dataRetentionCleanupJob() {
        return JobBuilder.newJob(DataRetentionCleanupJob.class)
            .withIdentity("dataRetentionCleanupJob")
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger dataRetentionCleanupTrigger() {
        return TriggerBuilder.newTrigger()
            .forJob(dataRetentionCleanupJob())
            .withIdentity("dataRetentionCleanupTrigger")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 3 ? * SUN *"))
            .build();
    }
}
