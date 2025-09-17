package com.loopers.application.ranking;

import com.loopers.domain.ranking.WeeklyRankingMetric;
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
public class WeeklyRankingReader extends JpaPagingItemReader<WeeklyRankingMetric> {

    public WeeklyRankingReader(EntityManagerFactory emf,
                               @Value("#{jobParameters['date']}") String date) {
        log.info("WeeklyRankingReader initialized with date: {}", date);
        setEntityManagerFactory(emf);
        setQueryString(
                "SELECT new com.loopers.domain.ranking.WeeklyRankingMetric(d.productId, " +
                        "SUM(d.viewCount * 0.1 + d.likeCount * 0.2 + d.salesCount * 0.7)) " +
                        "FROM DailyMetric d " +
                        "WHERE d.date >= :startDate " +
                        "GROUP BY d.productId " +
                        "ORDER BY SUM(d.viewCount * 0.1 + d.likeCount * 0.2 + d.salesCount * 0.7) DESC"
        );
        setParameterValues(Map.of("startDate", LocalDate.parse(date).minusDays(6)));
        setPageSize(300);
        setMaxItemCount(300);
    }
}
