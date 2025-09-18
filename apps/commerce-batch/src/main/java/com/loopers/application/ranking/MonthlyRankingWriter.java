package com.loopers.application.ranking;

import com.loopers.domain.ranking.MonthlyRankingScore;
import com.loopers.domain.ranking.RankingBuffer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@StepScope
@Component
@RequiredArgsConstructor
public class MonthlyRankingWriter implements ItemWriter<MonthlyRankingScore> {
    private final RankingBuffer rankingBuffer;

    @Override
    public void write(Chunk<? extends MonthlyRankingScore> chunk) throws Exception {
        for (MonthlyRankingScore item : chunk) {
            rankingBuffer.record(item);
        }
    }
}
