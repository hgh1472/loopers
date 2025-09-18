package com.loopers.application.ranking;

import com.loopers.domain.ranking.MonthlyRankingScore;
import com.loopers.domain.ranking.WeeklyProductRankMv;
import java.time.LocalDate;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class MonthlyRankingProcessor implements ItemProcessor<WeeklyProductRankMv, MonthlyRankingScore> {
    private final LocalDate date;

    public MonthlyRankingProcessor(@Value("#{jobParameters['date']}") LocalDate date) {
        this.date = date;
    }

    @Override
    public MonthlyRankingScore process(WeeklyProductRankMv item) throws Exception {
        // 오늘이면 가중치 1, 저번주면 가중치 0.8, 2주전이면 가중치 0.5, 3주전이면 가중치 0.2로 score 반영
        double weight = switch ((int) date.minusWeeks(3).until(item.getDate()).getDays()) {
            case 0 -> 1.0;
            case 7 -> 0.8;
            case 14 -> 0.5;
            case 21 -> 0.2;
            default -> 0.0;
        };
        return new MonthlyRankingScore(item.getProductId(), item.getScore() * weight);
    }
}
