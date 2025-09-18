package com.loopers.batch.job;

import com.loopers.application.ranking.MonthlyRankingJobListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RankingJobConfig {

    @Bean
    public Job rankingJob(JobRepository jobRepository,
                          Step weeklyRankingStep,
                          Step monthlyRankingStep,
                          MonthlyRankingJobListener listener) {
        return new JobBuilder("rankingJob", jobRepository)
                .start(weeklyRankingStep)
                .next(monthlyRankingStep)
                .listener(listener)
                .build();
    }
}
