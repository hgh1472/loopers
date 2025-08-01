package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductCriteria;
import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.ProductListResult;
import com.loopers.application.product.ProductResult;
import com.loopers.domain.PageResponse;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.ProductV1Dto.ProductPageResponse;
import com.loopers.interfaces.api.product.ProductV1Dto.ProductResponse;
import com.loopers.interfaces.api.product.ProductV1Dto.ProductSearchRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductV1Controller implements ProductV1ApiSpec {

    private final ProductFacade productFacade;

    @Override
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long productId,
                                                   @RequestHeader(value = "X-USER-ID", required = false) Long userId) {
        ProductResult productResult = productFacade.getProduct(new ProductCriteria.Get(productId, userId));
        return ApiResponse.success(ProductResponse.from(productResult));
    }

    @Override
    @GetMapping
    public ApiResponse<ProductPageResponse> searchProducts(@Valid @RequestBody ProductSearchRequest request,
                                                           @RequestHeader(value = "X-USER-ID", required = false) Long userId) {
        PageResponse<ProductListResult> results = productFacade.searchProducts(new ProductCriteria.Search(request.brandId(), userId,
                request.page(), request.size(), request.sort()));

        return ApiResponse.success(ProductPageResponse.from(results));
    }


}
