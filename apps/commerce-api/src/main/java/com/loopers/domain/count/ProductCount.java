package com.loopers.domain.count;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "product_count", indexes = {
        @Index(name = "idx_pc_like_ref", columnList = "like_count DESC, ref_product_id")
})
public class ProductCount extends BaseEntity {

    @Column(name = "ref_product_id", nullable = false, unique = true)
    private Long productId;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    protected ProductCount() {
    }

    private ProductCount(Long productId, Long likeCount) {
        this.productId = productId;
        this.likeCount = likeCount;
    }

    public static ProductCount from(Long productId) {
        if (productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품은 필수입니다.");
        }
        return new ProductCount(productId, 0L);
    }

    public void incrementLike() {
        this.likeCount++;
    }

    public void decrementLike() {
        if (this.likeCount <= 0) {
            throw new CoreException(ErrorType.CONFLICT, "더 이상 좋아요 수를 감소시킬 수 없습니다.");
        }
        this.likeCount--;
    }
}
