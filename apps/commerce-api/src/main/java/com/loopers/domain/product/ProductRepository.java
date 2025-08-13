package com.loopers.domain.product;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Slice;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findByIds(Set<Long> ids);

    Slice<ProductSearchView> search(ProductParams.Search params);

    Long countAllProducts();

    Long countBrandProducts(Long brandId);

    List<ProductSearchView> searchProducts(Set<Long> productIds);
}
