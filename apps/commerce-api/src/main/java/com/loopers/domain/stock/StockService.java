package com.loopers.domain.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public StockInfo findStock(StockCommand.Find command) {
        return stockRepository.findByProductId(command.productId())
                .map(StockInfo::from)
                .orElse(null);
    }
}
