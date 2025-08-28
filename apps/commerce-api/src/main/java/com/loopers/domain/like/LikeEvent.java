package com.loopers.domain.like;

public class LikeEvent {
    public record Liked(Long productId, Long userId) {
    }
}
