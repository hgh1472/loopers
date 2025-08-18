package com.loopers.interfaces.scheduler;

import com.loopers.domain.product.ProductCountProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCountScheduler {

    private final ProductCountProcessor productCountProcessor;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void updateCountCache() {
        productCountProcessor.updateCache();
    }
}
