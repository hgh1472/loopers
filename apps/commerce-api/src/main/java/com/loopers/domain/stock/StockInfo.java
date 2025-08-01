package com.loopers.domain.stock;

public record StockInfo(
    Long id,
    Long productId,
    Long quantity
) {
    public static StockInfo from(Stock stock) {
        return new StockInfo(
            stock.getId(),
            stock.getProductId(),
            stock.getQuantity().getValue()
        );
    }
}
