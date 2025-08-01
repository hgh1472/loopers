package com.loopers.application.brand;

import com.loopers.domain.brand.BrandInfo;

public record BrandResult(
        Long id,
        String name,
        String description
) {
    public static BrandResult from(BrandInfo brandInfo) {
        return new BrandResult(
                brandInfo.id(),
                brandInfo.name(),
                brandInfo.description()
        );
    }
}
