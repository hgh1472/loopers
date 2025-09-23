package com.loopers.application.ranking;

import com.loopers.domain.ranking.DailyMetric;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.batch.item.database.JpaPagingItemReader;

public class WeeklyRankingReader extends JpaPagingItemReader<DailyMetric> {

    public WeeklyRankingReader(EntityManagerFactory emf,
                               String date) {
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
