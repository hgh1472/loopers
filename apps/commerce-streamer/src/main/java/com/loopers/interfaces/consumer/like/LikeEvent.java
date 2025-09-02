package com.loopers.interfaces.consumer.like;

public class LikeEvent {
    public record Liked(
            String eventId,
            Long productId,
            Long memberId
    ) {
    }

    public record Canceled(
            String eventId,
            Long productId,
            Long memberId
    ) {
    }
}
