package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "product", indexes = {
        @Index(name = "idx_brand_id", columnList = "ref_brand_id"),
        @Index(name = "idx_product_search_created_at", columnList = "created_at DESC"),
        @Index(name = "idx_product_search_price", columnList = "price DESC"),
        @Index(name = "idx_brand_created_at", columnList = "ref_brand_id, created_at DESC"),
        @Index(name = "idx_brand_price", columnList = "ref_brand_id, price DESC")
})
public class Product extends BaseEntity {

    @Column(name = "ref_brand_id", nullable = false)
    private Long brandId;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "price", nullable = false, precision = 10, scale = 2))
    private Price price;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    protected Product() {
    }

    private Product(Long brandId, String name, Price price, ProductStatus status) {
        this.brandId = brandId;
        this.name = name;
        this.price = price;
        this.status = status;
    }

    public static Product create(ProductCommand.Create command) {
        if (command.brandId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "브랜드 ID는 필수입니다.");
        }
        Long brandId = command.brandId();

        if (command.name() == null || command.name().isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "제품 이름은 필수입니다.");
        }
        String name = command.name();

        Price price = new Price(command.price());

        ProductStatus status = ProductStatus.from(command.status());

        return new Product(brandId, name, price, status);
    }

    public boolean isPurchasable() {
        return this.status == ProductStatus.ON_SALE;
    }

    public enum ProductStatus {
        ON_SALE("판매중"), OUT_OF_STOCK("품절"), HOLD("판매 중지"), DELETED("삭제된 제품");

        private final String description;

        ProductStatus(String description) {
            this.description = description;
        }

        public static ProductStatus from(String status) {
            try {
                return ProductStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new CoreException(ErrorType.BAD_REQUEST, "유효하지 않은 제품 상태입니다.");
            }
        }
    }

    public Long getBrandId() {
        return brandId;
    }

    public String getName() {
        return name;
    }

    public Price getPrice() {
        return new Price(this.price.getValue());
    }

    public ProductStatus getStatus() {
        return status;
    }
}
