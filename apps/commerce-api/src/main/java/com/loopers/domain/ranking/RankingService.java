package com.loopers.domain.ranking;

import com.loopers.domain.PageResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingBoard rankingBoard;

    public PageResponse<RankingInfo> getRanking(RankingCommand.DailyRanking command) {
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
}
