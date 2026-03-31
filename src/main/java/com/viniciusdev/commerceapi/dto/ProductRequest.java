package com.viniciusdev.commerceapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductRequest (

        @NotBlank (message = "Name must not be blank")
        String name,

        String description,

        @PositiveOrZero(message = "Price must be positive or zero")
        BigDecimal price
){
}

