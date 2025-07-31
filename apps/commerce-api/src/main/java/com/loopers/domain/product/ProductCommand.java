package com.loopers.domain.product;

import java.math.BigDecimal;

public class ProductCommand {
    public record Create(
            Long brandId,
            String name,
            BigDecimal price,
            String status
    ) {
    }

    public record Find(Long productId) {
    }

    public record Page(
            Long brandId,
            Integer offset,
            Integer size,
            Sort sort
    ) {
        public enum Sort {
            LATEST, PRICE_ASC, LIKE_DESC
        }

        public static Sort from(String sort) {
            if (sort == null || sort.isBlank()) {
                return Sort.LATEST;
            }
            try {
                return Sort.valueOf(sort.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Sort.LATEST;
            }
        }
    }
}
