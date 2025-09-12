package com.loopers.interfaces.api.ranking;

import com.loopers.application.ranking.RankingResult;
import java.math.BigDecimal;

public class RankingV1Dto {
    public record RankingResponse(
            Long productId,
            String brandName,
            String productName,
            BigDecimal price,
            String status,
            Long rank
    ) {
        public static RankingResponse from(RankingResult result) {
            return new RankingResponse(
                    result.productId(),
                    result.brandName(),
                    result.productName(),
                    result.price(),
                    result.status(),
                    result.rank()
            );
        }
    }
}
