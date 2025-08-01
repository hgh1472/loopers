package com.loopers.application.like;

import com.loopers.domain.like.ProductLikeCommand;

public class LikeCriteria {
    public record Product(
            Long productId,
            Long userId
    ) {
        ProductLikeCommand.Create toLikeCreateCommand() {
            return new ProductLikeCommand.Create(userId, productId);
        }

        ProductLikeCommand.Delete toLikeDeleteCommand() {
            return new ProductLikeCommand.Delete(userId, productId);
        }
    }

    public record LikedProducts(
            Long userId
    ) {
    }
}
