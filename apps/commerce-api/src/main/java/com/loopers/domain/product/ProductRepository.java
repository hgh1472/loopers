package com.loopers.domain.product;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findByIds(Set<Long> ids);
}
