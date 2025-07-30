package com.loopers.domain.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public StockInfo findStock(StockCommand.Find command) {
        return stockRepository.findByProductId(command.productId())
                .map(StockInfo::from)
                .orElse(null);
    }
}
