package com.viniciusdev.commerceapi.exception;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorResponse(
        Instant timestamp,
        Integer status,
        String message,
        String path
) {
}
