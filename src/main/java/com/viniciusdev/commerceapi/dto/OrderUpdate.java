package com.viniciusdev.commerceapi.dto;

import com.viniciusdev.commerceapi.enums.OrderStatus;

public record OrderUpdate (
        OrderStatus status
) {

}
