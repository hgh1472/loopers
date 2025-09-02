package com.loopers.domain.metrics;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "product_metrics")
public class ProductMetrics extends BaseEntity {

    @Column(name = "ref_product_id", nullable = false, unique = true)
    private Long productId;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    protected ProductMetrics() {
    }

    public ProductMetrics(Long productId) {
        this.productId = productId;
        this.likeCount = 0L;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount <= 0) {
            throw new CoreException(ErrorType.CONFLICT, "좋아요 수는 0보다 작을 수 없습니다.");
        }
        this.likeCount--;
    }
}
