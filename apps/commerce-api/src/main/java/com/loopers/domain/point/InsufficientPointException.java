package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

public class InsufficientPointException extends CoreException {

    public InsufficientPointException(ErrorType errorType) {
        super(errorType);
    }

    public InsufficientPointException(ErrorType errorType, String customMessage) {
        super(errorType, customMessage);
    }
}
