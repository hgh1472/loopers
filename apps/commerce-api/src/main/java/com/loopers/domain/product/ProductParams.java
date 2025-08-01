package com.loopers.domain.product;

public class ProductParams {
    public record Search(
            Long brandId,
            int page,
            int size,
            Sort sort
    ) {
    }
    public enum Sort {
        LATEST, PRICE_ASC, LIKE_DESC;

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
