package com.loopers.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductLikeService {

    private final ProductLikeRepository productLikeRepository;

    public ProductLikeInfo like(ProductLikeCommand.Create command) {
        ProductLike productLike = ProductLike.create(command);
        try {
            return ProductLikeInfo.from(productLikeRepository.save(productLike));
        } catch (DataIntegrityViolationException e) {
            return ProductLikeInfo.from(productLike);
        }
    }

    @Transactional
    public ProductLikeInfo cancelLike(ProductLikeCommand.Delete command) {
        productLikeRepository.deleteByProductIdAndUserId(command.productId(), command.userId());
        return new ProductLikeInfo(command.productId(), command.userId());
    }

    public Long countLikes(ProductLikeCommand.Count command) {
        return productLikeRepository.countByProductId(command.productId());
    }

    public boolean isLiked(ProductLikeCommand.IsLiked command) {
        return productLikeRepository.existsByProductIdAndUserId(command.productId(), command.userId());
    }
}
