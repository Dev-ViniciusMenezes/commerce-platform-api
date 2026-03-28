package com.viniciusdev.commerceapi.dto;

public record OrderItemRequest (
        Long productId,
        Integer quantity
){
}
