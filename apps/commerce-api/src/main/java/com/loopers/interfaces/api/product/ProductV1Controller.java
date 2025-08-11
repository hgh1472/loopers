package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductCriteria;
import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.ProductResult;
import com.loopers.domain.PageResponse;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductV1Controller implements ProductV1ApiSpec {

    private final ProductFacade productFacade;

    @Override
    @GetMapping("/{productId}")
    public ApiResponse<ProductV1Dto.ProductResponse> getProduct(@PathVariable Long productId,
                                                                @RequestHeader(value = "X-USER-ID", required = false) Long userId) {
        ProductResult productResult = productFacade.getProduct(new ProductCriteria.Get(productId, userId));
        return ApiResponse.success(ProductV1Dto.ProductResponse.from(productResult));
    }

    @Override
    @GetMapping
    public ApiResponse<PageResponse<ProductV1Dto.ProductCard>> searchProducts(
            @RequestParam(required = false) Long brandId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LATEST") String sort,
            @RequestHeader(value = "X-USER-ID", required = false) Long userId) {
        PageResponse<ProductResult.Card> results = productFacade.searchProducts(
                new ProductCriteria.Search(brandId, userId,
                        page, size, sort));

        return ApiResponse.success(results.map(ProductV1Dto.ProductCard::from));
    }


}
