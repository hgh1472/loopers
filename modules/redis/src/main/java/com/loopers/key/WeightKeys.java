package com.loopers.key;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WeightKeys {
    WEIGHT("ranking:weights:current"),
    LIKE("likes"),
    VIEW("views"),
    SALES("sales");

    private final String key;

    public String getKey() {
        return key;
    }
}
