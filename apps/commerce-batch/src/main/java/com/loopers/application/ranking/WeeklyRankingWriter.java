package com.loopers.application.ranking;

import com.loopers.domain.ranking.RankingBuffer;
import com.loopers.domain.ranking.WeeklyRankingScore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
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
