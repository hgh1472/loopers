package com.loopers.application.ranking;

import com.loopers.domain.ranking.MonthlyRankingScore;
import com.loopers.domain.ranking.MonthlyWeight;
import com.loopers.domain.ranking.WeeklyProductRankMv;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.batch.item.ItemProcessor;

public class MonthlyRankingProcessor implements ItemProcessor<WeeklyProductRankMv, MonthlyRankingScore> {
    private final LocalDate date;

    public MonthlyRankingProcessor(String dateStr) {
        this.date = LocalDate.parse(dateStr);
    }

    @Override
    public MonthlyRankingScore process(WeeklyProductRankMv item) throws Exception {
        int weeksDiff = (int) ChronoUnit.WEEKS.between(item.getDate(), date);
        MonthlyWeight monthlyWeight = MonthlyWeight.fromWeeksDiff(weeksDiff);
        return new MonthlyRankingScore(item.getProductId(), item.getScore(), monthlyWeight.applyWeight(item.getScore()));
    }
}
