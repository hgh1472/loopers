package com.loopers.interfaces.api.brand;

import com.loopers.application.brand.BrandResult;

public class BrandV1Dto {
    public record BrandResponse(
            Long id,
            String name,
            String description
    ) {
        public static BrandResponse from(BrandResult brandResult) {
            return new BrandResponse(
                    brandResult.id(),
                    brandResult.name(),
                    brandResult.description()
            );
        }
    }
}
