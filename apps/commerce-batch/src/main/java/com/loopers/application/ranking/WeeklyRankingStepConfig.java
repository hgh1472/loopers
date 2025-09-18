package com.loopers.application.ranking;

import com.loopers.domain.ranking.MonthlyRankingScore;
import com.loopers.domain.ranking.WeeklyProductRankMv;
import com.loopers.domain.ranking.WeeklyRankingMetric;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class WeeklyRankingStepConfig {

    @Bean
    public Step weeklyRankingStep(JobRepository jobRepository,
                                  PlatformTransactionManager tm,
                                  WeeklyRankingReader reader,
                                  WeeklyRankingProcessor processor,
                                  WeeklyRankingWriter writer) {
        return new StepBuilder("weeklyRankingStep", jobRepository)
                .<WeeklyRankingMetric, WeeklyProductRankMv>chunk(300, tm)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step monthlyRankingStep(JobRepository jobRepository,
                                  PlatformTransactionManager tm,
                                  MonthlyRankingReader reader,
                                  MonthlyRankingProcessor processor,
                                  MonthlyRankingWriter writer) {
        return new StepBuilder("monthlyRankingStep", jobRepository)
                .<WeeklyProductRankMv, MonthlyRankingScore>chunk(300, tm)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
