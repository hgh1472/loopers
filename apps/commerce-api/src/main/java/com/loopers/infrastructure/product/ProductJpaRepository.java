package com.loopers.infrastructure.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductSearchView;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    @Query("SELECT new com.loopers.domain.product.ProductSearchView(p.id, p.brandId, b.name, p.name, p.price, p.status, pc.likeCount) "
            + "FROM Product p "
            + "JOIN ProductCount pc ON p.id = pc.productId "
            + "JOIN Brand b ON p.brandId = b.id "
            + "WHERE (:brandId IS NULL OR p.brandId = :brandId) "
            + "ORDER BY p.createdAt DESC")
    Page<ProductSearchView> searchLatestProducts(@Param("brandId") Long brandId, Pageable pageable);

    @Query("SELECT new com.loopers.domain.product.ProductSearchView(p.id, p.brandId, b.name, p.name, p.price, p.status, pc.likeCount) "
            + "FROM Product p "
            + "JOIN ProductCount pc ON p.id = pc.productId "
            + "JOIN Brand b ON p.brandId = b.id "
            + "WHERE (:brandId IS NULL OR p.brandId = :brandId) "
            + "ORDER BY p.price.value ASC")
    Page<ProductSearchView> searchPriceAscProducts(@Param("brandId") Long brandId, Pageable pageable);

    @Query("SELECT new com.loopers.domain.product.ProductSearchView(p.id, p.brandId, b.name, p.name, p.price, p.status, pc.likeCount) "
            + "FROM Product p "
            + "JOIN ProductCount pc ON p.id = pc.productId "
            + "JOIN Brand b ON p.brandId = b.id "
            + "WHERE (:brandId IS NULL OR p.brandId = :brandId) "
            + "ORDER BY pc.likeCount DESC")
    Page<ProductSearchView> searchLikeDescProducts(@Param("brandId") Long brandId, Pageable pageable);

    @Query("SELECT new com.loopers.domain.product.ProductSearchView(p.id, p.brandId, b.name, p.name, p.price, p.status, pc.likeCount) "
            + "FROM Product p "
            + "JOIN ProductCount pc ON p.id = pc.productId "
            + "JOIN Brand b ON p.brandId = b.id "
            + "WHERE p.id IN :productIds")
    List<ProductSearchView> searchAllByProductIds(Set<Long> productIds);
}
