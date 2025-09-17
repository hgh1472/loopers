package com.loopers.interfaces.scheduler;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job weeklyRankingJob;

    @Scheduled(cron = "0 * * * * ?")
    public void runWeeklyRankingJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", LocalDate.now().toString())
                .toJobParameters();

        try {
            jobLauncher.run(weeklyRankingJob, jobParameters);
        } catch (Exception e) {
            log.error("배치 실패 - weeklyRankingJob", e);
        }
    }
}
