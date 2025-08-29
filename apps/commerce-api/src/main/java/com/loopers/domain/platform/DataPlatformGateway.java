package com.loopers.domain.platform;

import java.util.concurrent.CompletableFuture;

public interface DataPlatformGateway {
    CompletableFuture<Void> send(PlatformCommand.Order order);

    CompletableFuture<Void> send(PlatformCommand.Payment payment);
}
