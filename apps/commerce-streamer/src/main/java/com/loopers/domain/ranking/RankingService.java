package com.loopers.domain.ranking;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingBoard rankingBoard;
    private final DailyRankingRepository dailyRankingRepository;
    private final WeightRepository weightRepository;
    private final RankingWeightCalculator rankingWeightCalculator;

    public void recordLikeCounts(List<RankingCommand.Like> cmd) {
        for (RankingCommand.Like like : cmd) {
            Double score = rankingWeightCalculator.calculateLikeScore(like.count(), like.date());
            rankingBoard.recordLikeCount(like.productId(), score, like.date());
        }
    }

    public void recordViewCounts(List<RankingCommand.View> cmd) {
        for (RankingCommand.View view : cmd) {
            Double score = rankingWeightCalculator.calculateViewScore(view.count(), view.date());
            rankingBoard.recordViewCount(view.productId(), score, view.date());
        }
    }

    public void recordSalesCounts(List<RankingCommand.Sale> cmd) {
        for (RankingCommand.Sale sales : cmd) {
            Double score = rankingWeightCalculator.calculateSalesScore(sales.count(), sales.date());
            rankingBoard.recordSalesCount(sales.productId(), score, sales.date());
        }
    }

    @Transactional
    public void updateDailyRankings(RankingCommand.UpdateDailyRanking cmd) {
        List<Long> topRankedProducts = rankingBoard.getTopRankedProducts(cmd.date(), 20);
        List<DailyRanking> dailyRankings = new ArrayList<>();
        for (int i = 0; i < topRankedProducts.size(); i++) {
            DailyRanking dailyRanking = new DailyRanking(topRankedProducts.get(i), i + 1, cmd.date());
            dailyRankings.add(dailyRanking);
        }
        dailyRankingRepository.saveAll(dailyRankings);

        Weight before = weightRepository.findActivateWeight();
        before.deactivate();
        Weight weight = weightRepository.findLatestWeight();
        weight.activate();
        LocalDate tomorrow = cmd.date().plusDays(1);
        rankingBoard.updateWeights(weight.getLikeWeight(), weight.getViewWeight(), weight.getSalesWeight(), tomorrow);

        rankingBoard.carryOverScores(cmd.date());
    }
}
