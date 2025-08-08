package com.loopers.infrastructure.count;

import com.loopers.domain.count.ProductCount;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ProductCountJpaRepository extends JpaRepository<ProductCount, Long> {
    Optional<ProductCount> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pc FROM ProductCount pc WHERE pc.productId = :productId")
    Optional<ProductCount> findByProductIdWithLock(Long productId);
}
