package com.loopers.domain.ranking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.Getter;

@Entity
@Getter
@Table(name = "mv_product_rank_weekly", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ref_product_id", "date"})
})
public class WeeklyProductRankMv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_product_id", nullable = false)
    private Long productId;

    @Column(name = "weekly_rank", nullable = false)
    private Integer rank;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "weighted_score", nullable = false)
    private Double weightedScore;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    protected WeeklyProductRankMv() {
    }

    public WeeklyProductRankMv(Long productId, Integer rank, Double score, Double weightedScore, LocalDate date) {
        this.productId = productId;
        this.rank = rank;
        this.score = score;
        this.weightedScore = weightedScore;
        this.date = date;
    }
}
