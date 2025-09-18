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
@Table(name = "mv_product_rank_monthly")
public class MonthlyProductRankMv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "ref_product_id", nullable = false)
    private Long productId;

    @Column(name = "monthly_rank", nullable = false)
    private Integer rank;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    protected MonthlyProductRankMv() {
    }

    public MonthlyProductRankMv(Long productId, Double score, Integer rank, LocalDate date) {
        this.productId = productId;
        this.score = score;
        this.rank = rank;
        this.date = date;
    }
}
