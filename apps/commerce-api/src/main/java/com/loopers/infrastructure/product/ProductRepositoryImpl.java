package com.loopers.infrastructure.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductParams;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.ProductSearchView;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public List<Product> findByIds(Set<Long> ids) {
        return productJpaRepository.findAllById(ids);
    }

    @Override
    public Page<ProductSearchView> search(ProductParams.Search params) {
        PageRequest pageRequest = PageRequest.of(params.page(), params.size());
        return switch (params.sort()) {
            case LATEST -> productJpaRepository.searchLatestProducts(params.brandId(), pageRequest);
            case PRICE_ASC -> productJpaRepository.searchPriceAscProducts(params.brandId(), pageRequest);
            case LIKE_DESC -> productJpaRepository.searchLikeDescProducts(params.brandId(), pageRequest);
        };
    }

    @Override
    public List<ProductSearchView> searchProducts(Set<Long> productIds) {
        return productJpaRepository.searchAllByProductIds(productIds);
    }
}
