package com.loopers.domain.ranking;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "weight")
public class Weight extends BaseEntity {
    @Column(name = "like_weight", nullable = false)
    private Double likeWeight;

    @Column(name = "view_weight", nullable = false)
    private Double viewWeight;

    @Column(name = "sales_weight", nullable = false)
    private Double salesWeight;

    @Column(name = "activate", nullable = false)
    private boolean activate;

    protected Weight() {
    }

    public Weight(Double likeWeight, Double viewWeight, Double salesWeight) {
        this.likeWeight = likeWeight;
        this.viewWeight = viewWeight;
        this.salesWeight = salesWeight;
        this.activate = false;
    }

    public void activate() {
        this.activate = true;
    }

    public void deactivate() {
        this.activate = false;
    }
}
