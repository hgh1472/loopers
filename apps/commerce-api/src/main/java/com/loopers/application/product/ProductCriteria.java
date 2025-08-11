package com.loopers.application.product;

import com.loopers.domain.product.ProductCommand;

public class ProductCriteria {
    public record Get(Long productId, Long userId) {
    }

    public record Search(Long brandId, Long userId, int page, int size, String sort) {
        public ProductCommand.Page toPageCommand() {
            return new ProductCommand.Page(brandId, page - 1, size, sort);
        }
    }
}
