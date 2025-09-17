package com.loopers.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class WeeklyRankingBatchJob {

    @Bean
    public Job weeklyRankingJob(JobRepository jobRepository, Step weeklyRankingStep) {
        return new JobBuilder("weeklyRankingJob", jobRepository)
                .start(weeklyRankingStep)
                .build();
    }
}
