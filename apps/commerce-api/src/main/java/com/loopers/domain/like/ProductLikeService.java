package com.loopers.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductLikeService {

    private final ProductLikeRepository productLikeRepository;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ProductLikeInfo like(ProductLikeCommand.Create command) {
        ProductLike productLike = ProductLike.create(command);
        try {
            productLikeRepository.save(productLike);
            return ProductLikeInfo.of(productLike, true);
        } catch (DataIntegrityViolationException e) {
            return ProductLikeInfo.of(productLike, false);
        }
    }

    @Transactional
    public ProductLikeInfo cancelLike(ProductLikeCommand.Delete command) {
        boolean deleted = productLikeRepository.deleteByProductIdAndUserId(command.productId(), command.userId());
        return new ProductLikeInfo(command.productId(), command.userId(), deleted);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(ProductLikeCommand.IsLiked command) {
        return productLikeRepository.existsByProductIdAndUserId(command.productId(), command.userId());
    }
}
