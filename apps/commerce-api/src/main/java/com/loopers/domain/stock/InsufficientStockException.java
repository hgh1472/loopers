package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

public class InsufficientStockException extends CoreException {

    public InsufficientStockException(ErrorType errorType) {
        super(errorType);
    }

    public InsufficientStockException(ErrorType errorType, String customMessage) {
        super(errorType, customMessage);
    }
}
