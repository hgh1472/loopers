package com.loopers.domain.ranking;


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
public class DailyMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_product_id", nullable = false)
    private Long productId;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "sales_count", nullable = false)
    private Long salesCount;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    protected DailyMetric() {
    }

    public DailyMetric(Long productId, Long likeCount, Long salesCount, Long viewCount, LocalDate date) {
        this.productId = productId;
        this.likeCount = likeCount;
        this.salesCount = salesCount;
        this.viewCount = viewCount;
        this.date = date;
    }

    public Double calculateScore() {
        return likeCount * 0.2 + salesCount * 1.0 + viewCount * 0.1;
    }
}
