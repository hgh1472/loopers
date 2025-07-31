package com.loopers.domain.like;

public record ProductLikeStateInfo(
        Long productId,
        Long userId,
        boolean isLiked
) {
}
