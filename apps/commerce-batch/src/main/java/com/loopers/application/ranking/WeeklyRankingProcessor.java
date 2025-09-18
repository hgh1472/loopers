package com.loopers.application.ranking;

import com.loopers.domain.ranking.DailyMetric;
import com.loopers.domain.ranking.RankingBuffer;
import com.loopers.domain.ranking.WeeklyRankingScore;
import com.loopers.domain.ranking.WeeklyWeight;
import java.time.LocalDate;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class WeeklyRankingProcessor implements ItemProcessor<DailyMetric, WeeklyRankingScore> {
    private final RankingBuffer rankingBuffer;
    private final LocalDate date;

    public WeeklyRankingProcessor(RankingBuffer rankingBuffer, @Value("#{jobParameters['date']}") String date) {
        this.rankingBuffer = rankingBuffer;
        this.date = LocalDate.parse(date);
    }

    @Override
    public WeeklyRankingScore process(DailyMetric item) {
        // date와 item.date의 일자 차이 계산
        int daysDiff = (int) (date.toEpochDay() - item.getDate().toEpochDay());
        WeeklyWeight weeklyWeight = WeeklyWeight.fromDaysDiff(daysDiff);
        return new WeeklyRankingScore(item.getProductId(), weeklyWeight.applyWeight(item.calculateScore()));
    }
}
