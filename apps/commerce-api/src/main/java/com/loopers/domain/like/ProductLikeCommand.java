package com.loopers.domain.like;

import java.util.Set;

public class ProductLikeCommand {

    public record Create(Long productId, Long userId) {
    }

    public record Delete(Long productId, Long userId) {
    }

    public record IsLiked(Long productId, Long userId) {
    }

    public record AreLiked(Set<Long> productIds, Long userId) {
    }
}
