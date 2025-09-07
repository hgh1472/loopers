package com.loopers.domain.cache;

public class CacheCommand {
    public record EvictProduct(
            Long productId
    ) {
    }
}
