package com.viniciusdev.commerceapi.exception;

public class PaymentAlreadyExistsException extends BusinessException {
    public PaymentAlreadyExistsException(String message) {
        super(message);
    }
}
