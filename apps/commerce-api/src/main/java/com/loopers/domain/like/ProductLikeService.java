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
    public LikeInfo.ProductAction like(ProductLikeCommand.Create command) {
        ProductLike productLike = ProductLike.create(command);
        try {
            productLikeRepository.save(productLike);
            return LikeInfo.ProductAction.of(productLike, true);
        } catch (DataIntegrityViolationException e) {
            return LikeInfo.ProductAction.of(productLike, false);
        }
    }

    @Transactional
    public LikeInfo.ProductAction cancelLike(ProductLikeCommand.Delete command) {
        boolean deleted = productLikeRepository.deleteByProductIdAndUserId(command.productId(), command.userId());
        return new LikeInfo.ProductAction(command.productId(), command.userId(), deleted);
    }

    @Transactional(readOnly = true)
    public LikeInfo.IsLiked isLiked(ProductLikeCommand.IsLiked command) {
        return new LikeInfo.IsLiked(productLikeRepository.existsByProductIdAndUserId(command.productId(), command.userId()));
    }

    @Transactional(readOnly = true)
    public List<LikeInfo.ProductState> areLiked(ProductLikeCommand.AreLiked command) {
        Set<Long> likedProductIds = productLikeRepository.findLikedProductIdsOf(command.userId(), command.productIds());

        return command.productIds().stream()
                .map(productId -> new LikeInfo.ProductState(
                        productId,
                        command.userId(),
                        likedProductIds.contains(productId)
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LikeInfo.Product> getMyProductLikes(ProductLikeCommand.Get command) {
        List<ProductLike> productLikes = productLikeRepository.findAllByUserId(command.userId());
        return productLikes.stream()
                .map(LikeInfo.Product::from)
                .collect(Collectors.toList());
    }
}
