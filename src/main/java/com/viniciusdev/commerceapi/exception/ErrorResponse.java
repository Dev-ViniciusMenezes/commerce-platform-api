package com.viniciusdev.commerceapi.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.Instant;
import java.util.Map;


@Builder
public record ErrorResponse(
        @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant timestamp,
        Integer status,
        String message,
        Map<String, String> errors,
        String path
) {
}
