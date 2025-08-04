package com.loopers.domain.stock;

import java.util.Optional;

public interface StockRepository {

    Stock save(Stock stock);

    Optional<Stock> findByProductId(Long productId);
}
