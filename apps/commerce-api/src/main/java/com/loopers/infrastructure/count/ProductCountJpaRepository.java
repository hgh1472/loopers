package com.loopers.infrastructure.count;

import com.loopers.domain.count.ProductCount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCountJpaRepository extends JpaRepository<ProductCount, Long> {
    Optional<ProductCount> findByProductId(Long productId);
}
