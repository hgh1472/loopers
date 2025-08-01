package com.loopers.domain.like;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Getter

@Table(name = "product_like", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_product", columnNames = {"ref_user_id", "ref_product_id"})
})
public class ProductLike extends BaseEntity {

    @Column(name = "ref_product_id", nullable = false)
    private Long productId;

    @Column(name = "ref_user_id", nullable = false)
    private Long userId;

    protected ProductLike() {
    }

    private ProductLike(Long productId, Long userId) {
        this.productId = productId;
        this.userId = userId;
    }

    public static ProductLike create(ProductLikeCommand.Create command) {
        if (command.productId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 ID는 필수입니다.");
        }
        if (command.userId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID는 필수입니다.");
        }

        return new ProductLike(command.productId(), command.userId());
    }
}
