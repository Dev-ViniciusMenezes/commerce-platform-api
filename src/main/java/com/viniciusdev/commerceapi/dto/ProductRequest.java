package com.viniciusdev.commerceapi.dto;

import java.math.BigDecimal;

public record ProductRequest (
        String name,
        String description,
        BigDecimal price
){
}

