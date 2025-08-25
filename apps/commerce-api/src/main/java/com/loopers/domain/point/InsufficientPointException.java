package com.loopers.domain.point;

import com.loopers.support.error.ErrorType;

public class InsufficientPointException extends Exception {

    private final ErrorType errorType;
    private final String customMessage;

    public InsufficientPointException(ErrorType errorType, String customMessage) {
        super(customMessage != null ? customMessage : errorType.getMessage());
        this.errorType = errorType;
        this.customMessage = customMessage;
    }
}
