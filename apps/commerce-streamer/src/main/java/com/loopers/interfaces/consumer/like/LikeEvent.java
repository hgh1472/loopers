package com.loopers.interfaces.consumer.like;

public class LikeEvent {
    public record Liked(
            Long productId,
            Long memberId
    ) {
    }

    public record Canceled(
            Long productId,
            Long memberId
    ) {
    }
}
