package com.loopers.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductLikeService {

    private final ProductLikeRepository productLikeRepository;

    public Long countLikes(ProductLikeCommand.Count command) {
        return productLikeRepository.countByProductId(command.productId());
    }

    public boolean isLiked(ProductLikeCommand.IsLiked command) {
        return productLikeRepository.existsByProductIdAndUserId(command.productId(), command.userId());
    }
}
