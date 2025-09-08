package com.loopers.domain.metrics;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;

@Entity
@Getter
@Table(name = "product_metrics")
public class ProductMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_product_id", nullable = false, unique = true)
    private Long productId;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "sales_count", nullable = false)
    private Long salesCount;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    protected ProductMetrics() {
    }

    public ProductMetrics(Long productId, LocalDate date) {
        this.productId = productId;
        this.date = date;
        this.likeCount = 0L;
        this.salesCount = 0L;
        this.viewCount = 0L;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void incrementLikeCount(Long count) {
        if (count <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "증가 수는 0보다 커야 합니다.");
        }
        this.likeCount += count;
    }

    public void decrementLikeCount() {
        if (this.likeCount <= 0) {
            throw new CoreException(ErrorType.CONFLICT, "좋아요 수는 0보다 작을 수 없습니다.");
        }
        this.likeCount--;
    }

    public void decrementLikeCount(Long count) {
        if (count <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "감소 수는 0보다 커야 합니다.");
        }
        if (this.likeCount - count < 0) {
            throw new CoreException(ErrorType.CONFLICT, "좋아요 수는 0보다 작을 수 없습니다.");
        }
        this.likeCount -= count;
    }

    public void incrementSalesCount(Long quantity) {
        if (quantity <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "판매 수량은 0보다 커야 합니다.");
        }
        this.salesCount += quantity;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}
