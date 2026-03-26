package com.viniciusdev.commerceapi.dto;

import com.viniciusdev.commerceapi.enums.OrderStatus;

import java.time.Instant;
import java.util.Set;

public record OrderResponse(
        Long id,
        Instant moment,
        OrderStatus status,
        Set<OrderItemResponse> items,
        Instant paymentMoment,
        UserResponse user
) {
}
