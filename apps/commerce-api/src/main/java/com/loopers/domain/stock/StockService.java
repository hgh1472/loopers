package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public StockInfo findStock(StockCommand.Find command) {
        return stockRepository.findByProductId(command.productId())
                .map(StockInfo::from)
                .orElse(null);
    }

    @Transactional
    public StockInfo deduct(StockCommand.Deduct command) {
        Stock stock = stockRepository.findByProductIdWithLock(command.productId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다."));
        stock.deduct(command.quantity());
        return StockInfo.from(stock);
    }

    @Transactional
    public List<StockInfo> deductAll(List<StockCommand.Deduct> commands) {
        return commands.stream()
                .sorted(Comparator.comparing(StockCommand.Deduct::productId))
                .map(this::deduct)
                .toList();
    }
}
