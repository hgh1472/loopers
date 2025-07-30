package com.loopers.domain.product;

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
}
