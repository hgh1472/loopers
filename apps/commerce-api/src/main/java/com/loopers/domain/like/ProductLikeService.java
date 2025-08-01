package com.loopers.domain.like;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
    public ProductLikeActionInfo like(ProductLikeCommand.Create command) {
        ProductLike productLike = ProductLike.create(command);
        try {
            productLikeRepository.save(productLike);
            return ProductLikeActionInfo.of(productLike, true);
        } catch (DataIntegrityViolationException e) {
            return ProductLikeActionInfo.of(productLike, false);
        }
    }

    @Transactional
    public ProductLikeActionInfo cancelLike(ProductLikeCommand.Delete command) {
        boolean deleted = productLikeRepository.deleteByProductIdAndUserId(command.productId(), command.userId());
        return new ProductLikeActionInfo(command.productId(), command.userId(), deleted);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(ProductLikeCommand.IsLiked command) {
        return productLikeRepository.existsByProductIdAndUserId(command.productId(), command.userId());
    }

    @Transactional(readOnly = true)
    public List<ProductLikeStateInfo> areLiked(ProductLikeCommand.AreLiked command) {
        List<ProductLike> productLikes = productLikeRepository.findProductLikesOf(command.userId(), command.productIds());

        Set<Long> likedProductIds = productLikes.stream()
                .map(ProductLike::getProductId)
                .collect(Collectors.toSet());

        return command.productIds().stream()
                .map(productId -> new ProductLikeStateInfo(
                        productId,
                        command.userId(),
                        likedProductIds.contains(productId)
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductLikeInfo> getMyProductLikes(ProductLikeCommand.Get command) {
        List<ProductLike> productLikes = productLikeRepository.findAllByUserId(command.userId());
        return productLikes.stream()
                .map(ProductLikeInfo::from)
                .collect(Collectors.toList());
    }
}
