package com.loopers.domain.ranking;

import com.loopers.domain.ranking.RankingCommand.Sale;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingScoreRecorder rankingScoreRecorder;
    private final RankingWeightCalculator rankingWeightCalculator;

    public void recordLikeCounts(List<RankingCommand.Like> cmd) {
        for (RankingCommand.Like like : cmd) {
            Double score = rankingWeightCalculator.calculateLikeScore(like.count());
            rankingScoreRecorder.recordLikeCount(like.productId(), score, like.date());
        }
    }

    public void recordViewCounts(List<RankingCommand.View> cmd) {
        for (RankingCommand.View view : cmd) {
            Double score = rankingWeightCalculator.calculateViewScore(view.count());
            rankingScoreRecorder.recordViewCount(view.productId(), score, view.date());
        }
    }

    public void recordSalesCounts(List<Sale> cmd) {
        for (Sale sales : cmd) {
            Double score = rankingWeightCalculator.calculateSalesScore(sales.count());
            rankingScoreRecorder.recordSalesCount(sales.productId(), score, sales.date());
        }
    }
}
