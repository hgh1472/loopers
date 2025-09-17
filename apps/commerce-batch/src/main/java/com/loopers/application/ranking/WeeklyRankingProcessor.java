package com.loopers.application.ranking;

import com.loopers.domain.ranking.RankingBoard;
import com.loopers.domain.ranking.WeeklyProductRankMv;
import com.loopers.domain.ranking.WeeklyRankingMetric;
import java.time.LocalDate;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class WeeklyRankingProcessor implements ItemProcessor<WeeklyRankingMetric, WeeklyProductRankMv> {
    private final RankingBoard rankingBoard;
    private final LocalDate date;

    public WeeklyRankingProcessor(RankingBoard rankingBoard, @Value("#{jobParameters['date']}") String date) {
        this.rankingBoard = rankingBoard;
        this.date = LocalDate.parse(date);
    }

    @Override
    public WeeklyProductRankMv process(WeeklyRankingMetric item) {
        rankingBoard.recordWeekly(item);
        return new WeeklyProductRankMv(item.productId(), item.score(), date.plusDays(1));
    }
}
