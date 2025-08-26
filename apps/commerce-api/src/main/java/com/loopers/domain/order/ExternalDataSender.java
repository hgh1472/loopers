package com.loopers.domain.order;

import java.util.concurrent.CompletableFuture;

public interface ExternalDataSender {
    CompletableFuture<Void> send(Order order);
}
