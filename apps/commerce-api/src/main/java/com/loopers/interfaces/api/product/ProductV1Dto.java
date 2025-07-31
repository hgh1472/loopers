package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductPageResult;
import com.loopers.application.product.ProductResult;
import com.loopers.domain.PageResponse;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public class ProductV1Dto {

    public record ProductSearchRequest(
            Long brandId,
            @Min(value = 1, message = "잘못된 페이지 요청입니다.") Integer page,
            Integer size,
            String sort
    ) {
    }

    public record ProductResponse(
            Long id,
            String brandName,
            String productName,
            BigDecimal price,
            String status,
            Long quantity,
            Long likeCount,
            boolean isLiked
    ) {
        public static ProductResponse from(ProductResult productResult) {
            return new ProductResponse(
                    productResult.id(),
                    productResult.brandName(),
                    productResult.productName(),
                    productResult.price(),
                    productResult.status(),
                    productResult.quantity(),
                    productResult.likeCount(),
                    productResult.isLiked()
            );
        }
    }

    public record ProductPageResponse(
            PageResponse<ProductSearchResponse> productPage
    ) {
        public static ProductPageResponse from(PageResponse<ProductPageResult> productPageResults) {
            return new ProductPageResponse(
                    productPageResults.map(ProductSearchResponse::from)
            );
        }

        public record ProductSearchResponse(
                Long id,
                String brandName,
                String productName,
                BigDecimal price,
                String status,
                Long likeCount,
                boolean isLiked
        ) {
            public static ProductSearchResponse from(ProductPageResult productPageResult) {
                return new ProductSearchResponse(
                        productPageResult.id(),
                        productPageResult.brandName(),
                        productPageResult.productName(),
                        productPageResult.price(),
                        productPageResult.status(),
                        productPageResult.likeCount(),
                        productPageResult.isLiked()
                );
            }
        }
    }
}
