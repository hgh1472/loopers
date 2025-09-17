package com.loopers.application.ranking;

import com.loopers.domain.ranking.RankMvRepository;
import com.loopers.domain.ranking.RankingBoard;
import com.loopers.domain.ranking.WeeklyProductRankMv;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyRankingWriter implements ItemWriter<WeeklyProductRankMv> {
    private final RankingBoard rankingBoard;
    private final RankMvRepository rankMvRepository;

    @Override
    public void write(Chunk<? extends WeeklyProductRankMv> chunk) throws Exception {
        List<WeeklyProductRankMv> items = new ArrayList<>();
        for (WeeklyProductRankMv item : chunk) {
            try {
                Integer rank = rankingBoard.getWeeklyRank(item.getProductId());
                item.setRank(rank);
                items.add(item);
            } catch (RuntimeException e) {
                log.error("주간 랭킹 배치 작업 중 오류 발생 - productId: {}", item.getProductId(), e);
            }
        }
        rankMvRepository.saveAll(items);
    }
}
