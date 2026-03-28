package com.viniciusdev.commerceapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viniciusdev.commerceapi.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record OrderResponse(
        Long id,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant moment,
        OrderStatus status,
        Set<OrderItemResponse> items,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant paymentMoment,
        UserResponse user,
        BigDecimal total
) { }
