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
public class MonthlyRankingProductMv {
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

    protected MonthlyRankingProductMv() {
    }

    public MonthlyRankingProductMv(Long productId, Integer rank, Double score, LocalDate date) {
        this.productId = productId;
        this.rank = rank;
        this.score = score;
        this.date = date;
    }
}
