package com.loopers.domain.stock;

public class StockCommand {

    public record Create(Long productId, Long quantity) {
    }

    public record Find(Long productId) {
    }

    public record Deduct(Long productId, Long quantity) {
    } {
    }
}
