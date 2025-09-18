package com.loopers.application.ranking;

import com.loopers.domain.ranking.DailyMetric;
import com.loopers.domain.ranking.WeeklyRankingScore;
import com.loopers.domain.ranking.WeeklyWeight;
import java.time.LocalDate;
import org.springframework.batch.item.ItemProcessor;

public class WeeklyRankingProcessor implements ItemProcessor<DailyMetric, WeeklyRankingScore> {
    private final LocalDate date;

    public WeeklyRankingProcessor(String date) {
        this.date = LocalDate.parse(date);
    }

    @Override
    public WeeklyRankingScore process(DailyMetric item) {
        int daysDiff = (int) (date.toEpochDay() - item.getDate().toEpochDay());
        WeeklyWeight weeklyWeight = WeeklyWeight.fromDaysDiff(daysDiff);
        return new WeeklyRankingScore(item.getProductId(), weeklyWeight.applyWeight(item.calculateScore()));
    }
}
