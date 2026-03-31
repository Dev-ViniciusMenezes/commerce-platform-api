package com.viniciusdev.commerceapi.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record OrderItemRequest (

        @NotNull (message = "Product id must not be null")
        Long productId,

        @NotNull (message = "Quantity must not be null")
        @PositiveOrZero(message = "Quantity must be positive or zero")
        Integer quantity
){
}
