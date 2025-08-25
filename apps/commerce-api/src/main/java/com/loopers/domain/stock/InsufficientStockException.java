package com.loopers.domain.stock;

import com.loopers.support.error.ErrorType;

public class InsufficientStockException extends Exception {

    private final ErrorType errorType;
    private final String customMessage;

    public InsufficientStockException(ErrorType errorType, String customMessage) {
        super(customMessage != null ? customMessage : errorType.getMessage());
        this.errorType = errorType;
        this.customMessage = customMessage;
    }
}
