package com.loopers.domain.like;

public class ProductLikeCommand {

    public record Create(Long productId, Long userId) {
    }

    public record Delete(Long productId, Long userId) {
    }

    public record Count(Long productId) {
    }

    public record IsLiked(Long productId, Long userId) {
    }
}
