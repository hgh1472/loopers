package com.loopers.domain.count;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCountService {

    private final ProductCountRepository productCountRepository;

    @Transactional(readOnly = true)
    public ProductCountInfo getProductCount(ProductCountCommand.Get command) {
        return productCountRepository.findBy(command.productId())
                .map(ProductCountInfo::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다."));
    }

    @Transactional
    public ProductCountInfo incrementLike(ProductCountCommand.Increment command) {
        ProductCount productCount = productCountRepository.findBy(command.productId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다."));
        productCount.incrementLike();
        return ProductCountInfo.from(productCount);
    }

    @Transactional
    public ProductCountInfo decrementLike(ProductCountCommand.Decrement command) {
        ProductCount productCount = productCountRepository.findBy(command.productId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다."));
        productCount.decrementLike();
        return ProductCountInfo.from(productCount);
    }

    @Transactional(readOnly = true)
    public List<ProductCountInfo> getProductCounts(ProductCountCommand.GetList command) {
        return productCountRepository.findByProductIds(command.productIds()).stream()
                .map(ProductCountInfo::from)
                .toList();
    }
}
