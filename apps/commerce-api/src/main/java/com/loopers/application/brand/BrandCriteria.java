package com.loopers.application.brand;

import com.loopers.domain.brand.BrandCommand;

public class BrandCriteria {
    public record Get(Long id) {
        public BrandCommand.Find toCommand() {
            return new BrandCommand.Find(id);
        }
    }
}
