package com.loopers.domain.brand;

public class BrandCommand {
    public record Create(String name, String description) {
    }

    public record Find(Long id) {
    }
}
