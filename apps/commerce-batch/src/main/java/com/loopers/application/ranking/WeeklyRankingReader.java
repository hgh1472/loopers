package com.loopers.application.ranking;

import com.loopers.domain.ranking.DailyMetric;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
public class WeeklyRankingReader extends JpaPagingItemReader<DailyMetric> {

    public WeeklyRankingReader(EntityManagerFactory emf,
                               @Value("#{jobParameters['date']}") String date) {
        setEntityManagerFactory(emf);
        setQueryString(
                "SELECT d " +
                        "FROM DailyMetric d " +
                        "WHERE d.date >= :startDate"
        );
        setParameterValues(Map.of("startDate", LocalDate.parse(date).minusDays(6)));
        setPageSize(500);
    }
}
