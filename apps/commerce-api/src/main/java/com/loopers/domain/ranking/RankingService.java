package com.loopers.domain.ranking;

import com.loopers.domain.PageResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingBoard rankingBoard;
    private final RankingMvRepository rankingMvRepository;

    public PageResponse<RankingInfo> getRankings(RankingCommand.Rankings command) {
        int page = Math.max(command.page(), 1);
        int size = Math.min(Math.max(command.size(), 5), 20);
        int offset = (page - 1) * size;

        List<Long> rankedProducts = rankingBoard.getRankedProducts(offset, size, command.date());
        List<RankingInfo> rankingInfos = new ArrayList<>();
        for (int i = 0; i < rankedProducts.size(); i++) {
            rankingInfos.add(new RankingInfo(rankedProducts.get(i), (long) (offset + i + 1)));
        }

        Long totalCount = rankingBoard.getTotalCount(command.date());
        int totalPages = totalCount % size == 0 ? (int) (totalCount / size) : (int) (totalCount / size) + 1;
        return new PageResponse<>(rankingInfos, page, size, totalCount, totalPages);
    }

    public PageResponse<RankingInfo> getWeeklyRankings(RankingCommand.Rankings command) {
        int page = Math.max(command.page(), 1);
        int size = Math.min(Math.max(command.size(), 5), 20);

        Page<WeeklyRankingProductMv> products = rankingMvRepository.findWeeklyRankingProducts(page, size, command.date());

        return PageResponse.from(products)
                .map(product -> new RankingInfo(product.getProductId(), product.getRank().longValue()));
    }

    public RankingInfo getProductRank(RankingCommand.Ranking command) {
        Long rank = rankingBoard.getProductRank(command.productId(), command.date());
        return new RankingInfo(command.productId(), rank);
    }
}
