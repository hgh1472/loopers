package com.loopers.application.ranking;

import com.loopers.domain.ranking.MonthlyRankingScore;
import com.loopers.domain.ranking.RankingBuffer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class MonthlyRankingWriter implements ItemWriter<MonthlyRankingScore> {
    private final RankingBuffer rankingBuffer;

    @Override
    public void write(Chunk<? extends MonthlyRankingScore> chunk) throws Exception {
        for (MonthlyRankingScore item : chunk) {
            rankingBuffer.recordMonthly(item);
        }
    }
}
