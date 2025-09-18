package com.loopers.application.ranking;

import com.loopers.domain.ranking.RankingBuffer;
import com.loopers.domain.ranking.WeeklyRankingScore;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class WeeklyRankingWriter implements ItemWriter<WeeklyRankingScore> {
    private final RankingBuffer rankingBuffer;

    @Override
    public void write(Chunk<? extends WeeklyRankingScore> chunk) throws Exception {
        for (WeeklyRankingScore weeklyRankingScore : chunk) {
            rankingBuffer.recordWeekly(weeklyRankingScore);
        }
    }
}
