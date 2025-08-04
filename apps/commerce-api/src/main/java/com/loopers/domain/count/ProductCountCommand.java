package com.loopers.domain.count;

import java.util.Set;

public class ProductCountCommand {
    public record Get(Long productId) {}

    public record Increment(Long productId) {}

    public record Decrement(Long productId) {}

    public record GetList(Set<Long> productIds) {}
}
