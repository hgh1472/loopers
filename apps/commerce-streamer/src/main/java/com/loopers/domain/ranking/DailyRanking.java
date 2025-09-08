package com.loopers.domain.ranking;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@Entity
@Table(name = "daily_ranking")
public class DailyRanking extends BaseEntity {

    @Column(name = "ref_product_id", nullable = false)
    private Long productId;

    @Column(name = "rank", nullable = false)
    private Integer rank;

    @Column(name = "ranking_date", nullable = false)
    private LocalDate date;

    protected DailyRanking() {
    }

    public DailyRanking(Long productId, Integer rank, LocalDate date) {
        this.productId = productId;
        this.rank = rank;
        this.date = date;
    }
}
