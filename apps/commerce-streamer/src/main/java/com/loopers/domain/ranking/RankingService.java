package com.loopers.domain.ranking;

import com.loopers.domain.ranking.RankingCommand.Sale;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingBoard rankingBoard;
    private final DailyRankingRepository dailyRankingRepository;
    private final RankingWeightCalculator rankingWeightCalculator;

    public void recordLikeCounts(List<RankingCommand.Like> cmd) {
        for (RankingCommand.Like like : cmd) {
            Double score = rankingWeightCalculator.calculateLikeScore(like.count());
            rankingBoard.recordLikeCount(like.productId(), score, like.date());
        }
    }

    public void recordViewCounts(List<RankingCommand.View> cmd) {
        for (RankingCommand.View view : cmd) {
            Double score = rankingWeightCalculator.calculateViewScore(view.count());
            rankingBoard.recordViewCount(view.productId(), score, view.date());
        }
    }

    public void recordSalesCounts(List<Sale> cmd) {
        for (Sale sales : cmd) {
            Double score = rankingWeightCalculator.calculateSalesScore(sales.count());
            rankingBoard.recordSalesCount(sales.productId(), score, sales.date());
        }
    }

    public void updateDailyRankings(RankingCommand.UpdateDailyRanking cmd) {
        List<Long> topRankedProducts = rankingBoard.getTopRankedProducts(cmd.date(), 20);

        List<DailyRanking> dailyRankings = new ArrayList<>();
        for (int i = 0; i < topRankedProducts.size(); i++) {
            DailyRanking dailyRanking = new DailyRanking(topRankedProducts.get(i), i+1, cmd.date());
            dailyRankings.add(dailyRanking);
        }
        dailyRankingRepository.saveAll(dailyRankings);

        rankingBoard.carryOverScores(cmd.date());
    }
}
