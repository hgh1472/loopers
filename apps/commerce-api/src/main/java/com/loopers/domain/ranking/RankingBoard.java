package com.loopers.domain.ranking;

import java.time.LocalDate;
import java.util.List;

public interface RankingBoard {
    List<Long> getRankedProducts(int offset, int limit, LocalDate date);

    Long getTotalCount(LocalDate date);
}
