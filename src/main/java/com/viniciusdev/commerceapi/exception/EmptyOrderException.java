package com.viniciusdev.commerceapi.exception;

public class EmptyOrderException extends BusinessException {
    public EmptyOrderException(String message) {
        super(message);
    }
}
