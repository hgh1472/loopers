package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductInfo findProduct(ProductCommand.Find command) {
        return productRepository.findById(command.productId())
                .map(ProductInfo::from)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ProductInfo> getProducts(Set<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 ID 목록이 비어 있습니다.");
        }

        List<Product> products = productRepository.findByIds(productIds);
        if (products.isEmpty() || products.size() != productIds.size()) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품이 포함되어 있습니다.");
        }

        return products.stream()
                .map(ProductInfo::from)
                .toList();
    }
}
