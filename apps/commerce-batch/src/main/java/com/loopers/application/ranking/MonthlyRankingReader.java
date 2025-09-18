package com.loopers.application.ranking;

import com.loopers.domain.ranking.WeeklyProductRankMv;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@StepScope
@Component
public class MonthlyRankingReader extends JpaPagingItemReader<WeeklyProductRankMv> {

    public MonthlyRankingReader(EntityManagerFactory emf,
                                @Value("#{jobParameters['date']}") String dateStr) {
        setEntityManagerFactory(emf);
        setQueryString("SELECT w "
                + "FROM WeeklyProductRankMv w "
                + "WHERE w.date IN (:d0, :d1, :d2, :d3)"
        );
        LocalDate date = LocalDate.parse(dateStr);
        Map<String, Object> params = new HashMap<>();
        params.put("d0", date);
        params.put("d1", date.minusWeeks(1));
        params.put("d2", date.minusWeeks(2));
        params.put("d3", date.minusWeeks(3));
        setParameterValues(params);
        setEntityManagerFactory(emf);
        setPageSize(300);
    }
}
