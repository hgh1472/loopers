package com.loopers.interfaces.scheduler;

import java.time.LocalDate;
import java.util.UUID;
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
    private final Job rankingJob;

    @Scheduled(cron = "0 50 23 * * ?")
    public void runWeeklyRankingJob() {
        String batchId = UUID.randomUUID().toString();
        String dateStr = LocalDate.now().toString();

        log.info("배치 시작 - rankingJob - batchId: {}, date: {}", batchId, dateStr);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", dateStr)
                .addString("batch-id", batchId)
                .toJobParameters();

        try {
            jobLauncher.run(rankingJob, jobParameters);
        } catch (Exception e) {
            log.error("배치 실패 - rankingJob", e);
        }
    }
}
