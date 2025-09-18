package com.loopers.application.ranking;

import com.loopers.domain.ranking.DailyMetric;
import com.loopers.domain.ranking.MonthlyRankingScore;
import com.loopers.domain.ranking.RankMvRepository;
import com.loopers.domain.ranking.RankingBuffer;
import com.loopers.domain.ranking.WeeklyProductRankMv;
import com.loopers.domain.ranking.WeeklyRankingScore;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class RankingStepConfig {
    @Bean
    public Step weeklyRankingStep(JobRepository jobRepository,
                                  PlatformTransactionManager tm,
                                  WeeklyRankingReader reader,
                                  WeeklyRankingProcessor processor,
                                  WeeklyRankingWriter writer,
                                  WeeklyRankingStepListener listener) {
        return new StepBuilder("weeklyRankingStep", jobRepository)
                .<DailyMetric, WeeklyRankingScore>chunk(500, tm)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(listener)
                .build();
    }

    @Bean
    public Step monthlyRankingStep(JobRepository jobRepository,
                                   PlatformTransactionManager tm,
                                   MonthlyRankingReader reader,
                                   MonthlyRankingProcessor processor,
                                   MonthlyRankingWriter writer,
                                   MonthlyRankingStepListener listener) {
        return new StepBuilder("monthlyRankingStep", jobRepository)
                .<WeeklyProductRankMv, MonthlyRankingScore>chunk(500, tm)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(listener)
                .build();
    }

    @Bean
    @StepScope
    public MonthlyRankingReader monthlyRankingReader(EntityManagerFactory emf,
                                                     @Value("#{jobParameters['date']}") String dateStr) {
        return new MonthlyRankingReader(emf, dateStr);
    }

    @Bean
    @StepScope
    public MonthlyRankingProcessor monthlyRankingProcessor(@Value("#{jobParameters['date']}") String dateStr) {
        return new MonthlyRankingProcessor(dateStr);
    }

    @Bean
    @StepScope
    public MonthlyRankingWriter monthlyRankingWriter(RankingBuffer rankingBuffer) {
        return new MonthlyRankingWriter(rankingBuffer);
    }

    @Bean
    @StepScope
    public MonthlyRankingStepListener monthlyRankingStepListener(RankingBuffer rankingBuffer, RankMvRepository rankMvRepository) {
        return new MonthlyRankingStepListener(rankingBuffer, rankMvRepository);
    }
}
